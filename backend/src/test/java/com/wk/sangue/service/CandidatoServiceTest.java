package com.wk.sangue.service;

import com.wk.sangue.dto.CandidatoInputDTO;
import com.wk.sangue.dto.DoadoresPorReceptorReportDTO;
import com.wk.sangue.dto.ImcFaixaEtariaReportDTO;
import com.wk.sangue.dto.ObesosPercentualReportDTO;
import com.wk.sangue.model.Candidato;
import com.wk.sangue.model.Endereco;
import com.wk.sangue.repository.CandidatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para a classe de serviço CandidatoService.
 */
@ExtendWith(MockitoExtension.class)
public class CandidatoServiceTest {

    @Mock
    private CandidatoRepository candidatoRepository;

    @InjectMocks
    private CandidatoService candidatoService;

    private List<Candidato> candidatosTeste;

    @BeforeEach
    void setUp() {
        candidatosTeste = new ArrayList<>();
        
        // Candidato 1: Masculino, Obeso (IMC = 85 / 1.60^2 = 33.2), O-, 36 anos, peso > 50 -> APTO a doar
        // Idade baseada no ano de 2026. Nascimento: 1990
        Endereco end1 = new Endereco("12345-678", "Rua A", 100, "Centro", "Cidade A", "SP");
        Candidato c1 = new Candidato("João Silva", "111.111.111-11", "12.345.678-9",
                LocalDate.of(1990, 5, 20), "Masculino", "Maria Silva", "José Silva",
                "joao@email.com", "(11) 3333-3333", "(11) 99999-9999", 1.60, 85.0, "O-", end1);
        candidatosTeste.add(c1);

        // Candidato 2: Feminino, Não Obeso (IMC = 60 / 1.70^2 = 20.7), A+, 26 anos, peso > 50 -> APTO a doar
        // Nascimento: 2000
        Endereco end2 = new Endereco("12345-678", "Rua B", 200, "Centro", "Cidade A", "SP");
        Candidato c2 = new Candidato("Maria Santos", "222.222.222-22", "98.765.432-1",
                LocalDate.of(2000, 1, 10), "Feminino", "Ana Santos", "Carlos Santos",
                "maria@email.com", "(11) 4444-4444", "(11) 98888-8888", 1.70, 60.0, "A+", end2);
        candidatosTeste.add(c2);

        // Candidato 3: Feminino, Não Obeso (IMC = 45 / 1.60^2 = 17.5), B+, 15 anos (Menor de idade), peso 45 (Abaixo de 50) -> INAPTO a doar
        // Nascimento: 2011
        Endereco end3 = new Endereco("54321-000", "Rua C", 300, "Bairro B", "Cidade B", "RJ");
        Candidato c3 = new Candidato("Ana Souza", "333.333.333-33", "55.555.555-5",
                LocalDate.of(2011, 8, 15), "Feminino", "Clara Souza", "Julio Souza",
                "ana@email.com", "(21) 2222-2222", "(21) 97777-7777", 1.60, 45.0, "B+", end3);
        candidatosTeste.add(c3);
    }

    @Test
    void testCalcularIdade() {
        LocalDate dataNascimento = LocalDate.now().minusYears(30);
        int idadeCalculada = candidatoService.calcularIdade(dataNascimento);
        assertEquals(30, idadeCalculada);
    }

    @Test
    void testImportarCandidatosNovos() {
        CandidatoInputDTO inputDto = new CandidatoInputDTO();
        inputDto.setNome("Novo Candidato");
        inputDto.setCpf("999.999.999-99");
        inputDto.setRg("99.999.999-9");
        inputDto.setDataNasc("15/10/1985");
        inputDto.setSexo("Masculino");
        inputDto.setMae("Mae");
        inputDto.setPai("Pai");
        inputDto.setEmail("novo@email.com");
        inputDto.setCep("11111-111");
        inputDto.setEndereco("Rua D");
        inputDto.setNumero(400);
        inputDto.setBairro("Bairro C");
        inputDto.setCidade("Cidade C");
        inputDto.setEstado("PR");
        inputDto.setTelefoneFixo("(41) 5555-5555");
        inputDto.setCelular("(41) 95555-5555");
        inputDto.setAltura(1.80);
        inputDto.setPeso(80.0);
        inputDto.setTipoSanguineo("AB+");

        when(candidatoRepository.existsByCpf("999.999.999-99")).thenReturn(false);
        when(candidatoRepository.save(any(Candidato.class))).thenReturn(new Candidato());

        int importados = candidatoService.importarCandidatos(Arrays.asList(inputDto));

        assertEquals(1, importados);
        verify(candidatoRepository, times(1)).save(any(Candidato.class));
    }

    @Test
    void testImportarCandidatosDuplicadosIgnora() {
        CandidatoInputDTO inputDto = new CandidatoInputDTO();
        inputDto.setCpf("111.111.111-11");

        when(candidatoRepository.existsByCpf("111.111.111-11")).thenReturn(true);

        int importados = candidatoService.importarCandidatos(Arrays.asList(inputDto));

        assertEquals(0, importados);
        verify(candidatoRepository, never()).save(any(Candidato.class));
    }

    @Test
    void testGetPercentualObesosPorSexo() {
        when(candidatoRepository.findAll()).thenReturn(candidatosTeste);

        ObesosPercentualReportDTO report = candidatoService.getPercentualObesosPorSexo();

        // 1 Homem, 1 obeso -> 100%
        assertEquals(100.0, report.getPercentualHomens());
        // 2 Mulheres, 0 obesas -> 0%
        assertEquals(0.0, report.getPercentualMulheres());
    }

    @Test
    void testGetQuantidadeDoadoresPorReceptor() {
        when(candidatoRepository.findAll()).thenReturn(candidatosTeste);

        List<DoadoresPorReceptorReportDTO> reports = candidatoService.getQuantidadeDoadoresPorReceptor();

        // c1 (O-, 36 anos, peso 85) -> Apto (Doadores permitidos para todos, O- doa pra todos)
        // c2 (A+, 26 anos, peso 60) -> Apto (Pode doar para A+ e AB+)
        // c3 (B+, 15 anos, peso 45) -> Inapto (Idade < 16, Peso < 50)
        
        // Verifica o receptor AB+ (Universal)
        DoadoresPorReceptorReportDTO abPlus = reports.stream()
                .filter(r -> "AB+".equals(r.getTipoSanguineoReceptor()))
                .findFirst().orElse(null);
        assertNotNull(abPlus);
        assertEquals(2, abPlus.getQuantidadeDoadores()); // c1 (O-) e c2 (A+)

        // Verifica o receptor O- (Só recebe de O-)
        DoadoresPorReceptorReportDTO oMinus = reports.stream()
                .filter(r -> "O-".equals(r.getTipoSanguineoReceptor()))
                .findFirst().orElse(null);
        assertNotNull(oMinus);
        assertEquals(1, oMinus.getQuantidadeDoadores()); // Apenas c1 (O-)
    }
}
