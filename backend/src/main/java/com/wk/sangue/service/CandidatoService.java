package com.wk.sangue.service;

import com.wk.sangue.dto.*;
import com.wk.sangue.model.Candidato;
import com.wk.sangue.model.Endereco;
import com.wk.sangue.repository.CandidatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável por processar as regras de negócio dos candidatos e relatórios.
 */
@Service
public class CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    public CandidatoService(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    /**
     * Importa uma lista de candidatos DTO, convertendo e salvando no banco de dados.
     * Ignora registros cujo CPF já esteja cadastrado.
     *
     * @param dtos Lista de candidatos recebida no JSON
     * @return Quantidade de candidatos importados com sucesso
     */
    @Transactional
    public int importarCandidatos(List<CandidatoInputDTO> dtos) {
        int importados = 0;

        for (CandidatoInputDTO dto : dtos) {
            // Limpa o CPF para consistência antes de verificar no banco
            String cpfLimpo = dto.getCpf().trim();

            if (candidatoRepository.existsByCpf(cpfLimpo)) {
                continue; // Evita duplicados
            }

            // Normalização: Criação da entidade Endereco
            Endereco endereco = new Endereco(
                    dto.getCep(),
                    dto.getEndereco(),
                    dto.getNumero(),
                    dto.getBairro(),
                    dto.getCidade(),
                    dto.getEstado()
            );

            // Tratamento e limpeza da data com barras invertidas escapadas no JSON original (ex: 23\/05\/1964)
            String dataNascStr = dto.getDataNasc().replace("\\", "").trim();
            LocalDate dataNasc = LocalDate.parse(dataNascStr, DATE_FORMATTER);

            // Criação do Candidato com relacionamento
            Candidato candidato = new Candidato(
                    dto.getNome(),
                    cpfLimpo,
                    dto.getRg(),
                    dataNasc,
                    dto.getSexo(),
                    dto.getMae(),
                    dto.getPai(),
                    dto.getEmail(),
                    dto.getTelefoneFixo(),
                    dto.getCelular(),
                    dto.getAltura(),
                    dto.getPeso(),
                    dto.getTipoSanguineo(),
                    endereco
            );

            candidatoRepository.save(candidato);
            importados++;
        }

        return importados;
    }

    /**
     * Relatório 1: Conta quantos candidatos existem por estado.
     */
    @Transactional(readOnly = true)
    public List<EstadoReportDTO> getCandidatosPorEstado() {
        List<Candidato> candidatos = candidatoRepository.findAll();
        Map<String, Long> agrupado = candidatos.stream()
                .collect(Collectors.groupingBy(c -> c.getEndereco().getEstado(), Collectors.counting()));

        List<EstadoReportDTO> lista = new ArrayList<>();
        agrupado.forEach((estado, qtd) -> lista.add(new EstadoReportDTO(estado, qtd)));
        lista.sort(Comparator.comparing(EstadoReportDTO::getEstado));
        return lista;
    }

    /**
     * Relatório 2: Calcula o IMC médio por faixa etária de 10 em 10 anos.
     */
    @Transactional(readOnly = true)
    public List<ImcFaixaEtariaReportDTO> getImcMedioPorFaixaEtaria() {
        List<Candidato> candidatos = candidatoRepository.findAll();
        Map<String, List<Double>> imcsPorFaixa = new HashMap<>();

        for (Candidato c : candidatos) {
            int idade = calcularIdade(c.getDataNasc());
            String faixa = obterNomeFaixaEtaria(idade);
            double imc = c.getPeso() / (c.getAltura() * c.getAltura());

            imcsPorFaixa.computeIfAbsent(faixa, k -> new ArrayList<>()).add(imc);
        }

        List<ImcFaixaEtariaReportDTO> lista = new ArrayList<>();
        imcsPorFaixa.forEach((faixa, imcs) -> {
            double media = imcs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            lista.add(new ImcFaixaEtariaReportDTO(faixa, media));
        });

        // Ordena as faixas etárias pelo início da faixa (ex: "0 a 10", "11 a 20", etc.)
        lista.sort(Comparator.comparingInt(dto -> {
            String[] partes = dto.getFaixaEtaria().split(" a ");
            return Integer.parseInt(partes[0]);
        }));

        return lista;
    }

    /**
     * Relatório 3: Calcula o percentual de obesos entre homens e mulheres.
     */
    @Transactional(readOnly = true)
    public ObesosPercentualReportDTO getPercentualObesosPorSexo() {
        List<Candidato> candidatos = candidatoRepository.findAll();
        
        long totalHomens = 0;
        long obesosHomens = 0;
        long totalMulheres = 0;
        long obesosMulheres = 0;

        for (Candidato c : candidatos) {
            double imc = c.getPeso() / (c.getAltura() * c.getAltura());
            boolean eObeso = imc > 30.0;

            if ("Masculino".equalsIgnoreCase(c.getSexo())) {
                totalHomens++;
                if (eObeso) {
                    obesosHomens++;
                }
            } else if ("Feminino".equalsIgnoreCase(c.getSexo())) {
                totalMulheres++;
                if (eObeso) {
                    obesosMulheres++;
                }
            }
        }

        double pctHomens = totalHomens > 0 ? ((double) obesosHomens / totalHomens) * 100.0 : 0.0;
        double pctMulheres = totalMulheres > 0 ? ((double) obesosMulheres / totalMulheres) * 100.0 : 0.0;

        return new ObesosPercentualReportDTO(pctHomens, pctMulheres);
    }

    /**
     * Relatório 4: Calcula a média de idade por tipo sanguíneo.
     */
    @Transactional(readOnly = true)
    public List<IdadeTipoSanguineoReportDTO> getIdadeMediaPorTipoSanguineo() {
        List<Candidato> candidatos = candidatoRepository.findAll();
        Map<String, List<Integer>> idadesPorTipo = new HashMap<>();

        for (Candidato c : candidatos) {
            int idade = calcularIdade(c.getDataNasc());
            idadesPorTipo.computeIfAbsent(c.getTipoSanguineo(), k -> new ArrayList<>()).add(idade);
        }

        List<IdadeTipoSanguineoReportDTO> lista = new ArrayList<>();
        idadesPorTipo.forEach((tipo, idades) -> {
            double media = idades.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
            lista.add(new IdadeTipoSanguineoReportDTO(tipo, media));
        });

        lista.sort(Comparator.comparing(IdadeTipoSanguineoReportDTO::getTipoSanguineo));
        return lista;
    }

    /**
     * Relatório 5: Calcula a quantidade de doadores possíveis para cada tipo sanguíneo receptor.
     * Regras: Idade de 16 a 69 anos, peso acima de 50 Kg.
     */
    @Transactional(readOnly = true)
    public List<DoadoresPorReceptorReportDTO> getQuantidadeDoadoresPorReceptor() {
        List<Candidato> candidatos = candidatoRepository.findAll();

        // Filtra os candidatos aptos a doar sangue (idade de 16 a 69 anos e peso > 50 Kg)
        List<Candidato> doadoresAptos = candidatos.stream()
                .filter(c -> {
                    int idade = calcularIdade(c.getDataNasc());
                    return idade >= 16 && idade <= 69 && c.getPeso() > 50.0;
                })
                .collect(Collectors.toList());

        // Mapa de compatibilidade de recepção: Receptor -> Lista de tipos sanguíneos compatíveis
        Map<String, List<String>> compatibilidadeRecepcao = new HashMap<>();
        compatibilidadeRecepcao.put("A+", Arrays.asList("A+", "A-", "O+", "O-"));
        compatibilidadeRecepcao.put("A-", Arrays.asList("A-", "O-"));
        compatibilidadeRecepcao.put("B+", Arrays.asList("B+", "B-", "O+", "O-"));
        compatibilidadeRecepcao.put("B-", Arrays.asList("B-", "O-"));
        compatibilidadeRecepcao.put("AB+", Arrays.asList("A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-"));
        compatibilidadeRecepcao.put("AB-", Arrays.asList("A-", "B-", "O-", "AB-"));
        compatibilidadeRecepcao.put("O+", Arrays.asList("O+", "O-"));
        compatibilidadeRecepcao.put("O-", Collections.singletonList("O-"));

        List<DoadoresPorReceptorReportDTO> lista = new ArrayList<>();

        for (String receptor : compatibilidadeRecepcao.keySet()) {
            List<String> doadoresPermitidos = compatibilidadeRecepcao.get(receptor);
            long totalDoadores = doadoresAptos.stream()
                    .filter(d -> doadoresPermitidos.contains(d.getTipoSanguineo()))
                    .count();

            lista.add(new DoadoresPorReceptorReportDTO(receptor, totalDoadores));
        }

        lista.sort(Comparator.comparing(DoadoresPorReceptorReportDTO::getTipoSanguineoReceptor));
        return lista;
    }

    /**
     * Calcula a idade de uma pessoa baseado na data atual local.
     */
    public int calcularIdade(LocalDate dataNasc) {
        if (dataNasc == null) return 0;
        return Period.between(dataNasc, LocalDate.now()).getYears();
    }

    /**
     * Classifica a idade em faixas etárias de 10 em 10 anos.
     * Exemplo: 0 a 10, 11 a 20, 21 a 30, etc.
     */
    private String obterNomeFaixaEtaria(int idade) {
        if (idade <= 10) {
            return "0 a 10";
        }
        int g = (idade - 1) / 10;
        return (g * 10 + 1) + " a " + (g * 10 + 10);
    }
}
