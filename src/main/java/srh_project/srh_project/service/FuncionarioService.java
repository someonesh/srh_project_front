package srh_project.srh_project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class FuncionarioService {
    
    // ==============================================
    // CONFIGURAÇÃO DOS ENDPOINTS
    // ==============================================
    
    @Value("${api.base.url}")
    private String API_BASE;
    
    private String FUNCIONARIOS_URL;
    private String VENDEDORES_URL;
    private String FOLHA_URL;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @jakarta.annotation.PostConstruct
    public void init() {
        this.FUNCIONARIOS_URL = API_BASE + "/funcionarios";
        this.VENDEDORES_URL = API_BASE + "/vendedores";
        this.FOLHA_URL = API_BASE + "/folha";
    }
    // ==============================================
    // FUNCIONÁRIOS - CONSULTAS
    // ==============================================
    
    /**
     * Listar todos os funcionários
     */
    public List<Map<String, Object>> listarTodos() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                FUNCIONARIOS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar todos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Listar apenas funcionários ativos
     */
    public List<Map<String, Object>> listarAtivos() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                FUNCIONARIOS_URL + "/ativos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar ativos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Listar apenas funcionários inativos
     */
    public List<Map<String, Object>> listarInativos() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                FUNCIONARIOS_URL + "/inativos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar inativos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Buscar funcionário por ID
     */
    public Map<String, Object> buscarPorId(Long id) {
        try {
            return restTemplate.getForObject(FUNCIONARIOS_URL + "/" + id, Map.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao buscar por ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Buscar funcionário por email
     */
    public Map<String, Object> buscarPorEmail(String email) {
        try {
            return restTemplate.getForObject(FUNCIONARIOS_URL + "/email/" + email, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar por email: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Buscar funcionários por departamento
     */
    public List<Map<String, Object>> buscarPorDepartamento(String departamento) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                FUNCIONARIOS_URL + "/departamento/" + departamento,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao buscar por departamento: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Buscar funcionários por nome
     */
    public List<Map<String, Object>> buscarPorNome(String nome) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                FUNCIONARIOS_URL + "/buscar?nome=" + nome,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao buscar por nome: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Listar aniversariantes do mês
     */
    public List<Map<String, Object>> buscarAniversariantes() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                FUNCIONARIOS_URL + "/aniversariantes",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao buscar aniversariantes: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Resumo geral da empresa
     */
    public Map<String, Object> getResumoGeral() {
        try {
            return restTemplate.getForObject(FUNCIONARIOS_URL + "/resumo", Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar resumo: " + e.getMessage());
            Map<String, Object> resumo = new HashMap<>();
            resumo.put("total", contarTotal());
            resumo.put("ativos", contarAtivos());
            resumo.put("inativos", contarInativos());
            resumo.put("folhaMensal", calcularFolhaMensal());
            return resumo;
        }
    }
    
    // ==============================================
    // FUNCIONÁRIOS - OPERAÇÕES CRUD
    // ==============================================
    
    /**
     * Salvar novo funcionário
     */
    public Map<String, Object> salvar(Map<String, Object> funcionario) {
        try {
            return restTemplate.postForObject(FUNCIONARIOS_URL, funcionario, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return erro;
        }
    }
    
    /**
     * Atualizar funcionário
     */
    public void atualizar(Long id, Map<String, Object> funcionario) {
        try {
            restTemplate.put(FUNCIONARIOS_URL + "/" + id, funcionario);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar: " + e.getMessage());
        }
    }
    
    /**
     * Demitir funcionário
     */
    public void demitir(Long id) {
        try {
            restTemplate.patchForObject(FUNCIONARIOS_URL + "/" + id + "/demitir", null, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao demitir: " + e.getMessage());
        }
    }
    
    /**
     * Reativar funcionário
     */
    public void reativar(Long id) {
        try {
            restTemplate.patchForObject(FUNCIONARIOS_URL + "/" + id + "/reativar", null, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao reativar: " + e.getMessage());
        }
    }
    
    /**
     * Deletar funcionário (permanente)
     */
    public void deletar(Long id) {
        try {
            restTemplate.delete(FUNCIONARIOS_URL + "/" + id);
        } catch (Exception e) {
            System.err.println("Erro ao deletar: " + e.getMessage());
        }
    }
    
    // ==============================================
    // VENDEDORES - CONSULTAS
    // ==============================================
    
    /**
     * Listar todos os vendedores (ativos e inativos)
     */
    public List<Map<String, Object>> listarTodosVendedores() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                VENDEDORES_URL + "/todos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar vendedores: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Listar apenas vendedores ativos
     */
    public List<Map<String, Object>> listarVendedores() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                VENDEDORES_URL + "/ativos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar vendedores ativos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Listar vendedores inativos
     */
    public List<Map<String, Object>> listarVendedoresInativos() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                VENDEDORES_URL + "/inativos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar vendedores inativos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Buscar vendedor por código
     */
    public Map<String, Object> buscarVendedorPorCodigo(String codigo) {
        try {
            return restTemplate.getForObject(VENDEDORES_URL + "/codigo/" + codigo, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar vendedor por código: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Buscar vendedores por nome
     */
    public List<Map<String, Object>> buscarVendedoresPorNome(String nome) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                VENDEDORES_URL + "/buscar?nome=" + nome,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao buscar vendedores por nome: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // ==============================================
    // VENDEDORES - COMISSÕES E METAS
    // ==============================================
    
    /**
     * Calcular comissão de um vendedor
     */
    public Map<String, Object> calcularComissao(Long id, Double totalVendas, boolean detalhado) {
        try {
            String url = VENDEDORES_URL + "/" + id + "/comissao?totalVendas=" + totalVendas;
            if (detalhado) {
                url += "&detalhado=true";
            }
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao calcular comissão: " + e.getMessage());
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("comissao", totalVendas * 0.05);
            resultado.put("percentual", 5.0);
            return resultado;
        }
    }
    
    /**
     * Calcular comissão por código do vendedor
     */
    public Map<String, Object> calcularComissaoPorCodigo(String codigo, Double totalVendas) {
        try {
            String url = VENDEDORES_URL + "/comissao?codigo=" + codigo + "&totalVendas=" + totalVendas;
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao calcular comissão por código: " + e.getMessage());
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("comissao", totalVendas * 0.05);
            resultado.put("percentual", 5.0);
            return resultado;
        }
    }
    
    /**
     * Configurar percentual de comissão
     */
    public void configurarComissao(Long id, Double comissao) {
        try {
            restTemplate.patchForObject(
                VENDEDORES_URL + "/" + id + "/configurar-comissao?comissao=" + comissao,
                null,
                Map.class
            );
        } catch (Exception e) {
            System.err.println("Erro ao configurar comissão: " + e.getMessage());
        }
    }
    
    /**
     * Remover comissão (voltar a null)
     */
    public void removerComissao(Long id) {
        try {
            restTemplate.patchForObject(
                VENDEDORES_URL + "/" + id + "/remover-comissao",
                null,
                Map.class
            );
        } catch (Exception e) {
            System.err.println("Erro ao remover comissão: " + e.getMessage());
        }
    }
    
    /**
     * Verificar se vendedor atingiu a meta
     */
    public Map<String, Object> verificarMeta(Long id, Double totalVendas) {
        try {
            String url = VENDEDORES_URL + "/" + id + "/verificar-meta?totalVendas=" + totalVendas;
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao verificar meta: " + e.getMessage());
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("atingiu", totalVendas >= 50000);
            resultado.put("meta", 50000);
            return resultado;
        }
    }
    
    /**
     * Atualizar meta mensal do vendedor
     */
    public void atualizarMeta(Long id, Double novaMeta) {
        try {
            restTemplate.patchForObject(
                VENDEDORES_URL + "/" + id + "/meta?novaMeta=" + novaMeta,
                null,
                Map.class
            );
        } catch (Exception e) {
            System.err.println("Erro ao atualizar meta: " + e.getMessage());
        }
    }
    
    /**
     * Ativar vendedor
     */
    public void ativarVendedor(Long id) {
        try {
            restTemplate.patchForObject(VENDEDORES_URL + "/" + id + "/ativar", null, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao ativar vendedor: " + e.getMessage());
        }
    }
    
    /**
     * Inativar vendedor
     */
    public void inativarVendedor(Long id) {
        try {
            restTemplate.patchForObject(VENDEDORES_URL + "/" + id + "/inativar", null, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao inativar vendedor: " + e.getMessage());
        }
    }
    
    /**
     * Criar vendedor simplificado
     */
    public Map<String, Object> criarVendedorSimples(String nome, String email) {
        try {
            String url = VENDEDORES_URL + "/simples?nome=" + nome + "&email=" + email;
            return restTemplate.postForObject(url, null, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao criar vendedor simples: " + e.getMessage());
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return erro;
        }
    }
    
    /**
     * Estatísticas dos vendedores
     */
    public Map<String, Object> getEstatisticasVendedores() {
        try {
            return restTemplate.getForObject(VENDEDORES_URL + "/estatisticas", Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar estatísticas de vendedores: " + e.getMessage());
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", listarVendedores().size());
            stats.put("ativos", listarVendedores().size());
            stats.put("inativos", 0);
            return stats;
        }
    }
    
    /**
     * Relatório de vendedores
     */
    public List<Map<String, Object>> getRelatorioVendedores() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                VENDEDORES_URL + "/relatorio",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao buscar relatório de vendedores: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // ==============================================
    // FOLHA DE PAGAMENTO
    // ==============================================
    
    /**
     * Processar folha de todos os funcionários
     */
    public Map<String, Object> processarFolha(int mes, int ano) {
        try {
            String url = FOLHA_URL + "/processar?mes=" + mes + "&ano=" + ano;
            return restTemplate.postForObject(url, null, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao processar folha: " + e.getMessage());
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("status", "simulado");
            resultado.put("mes", mes);
            resultado.put("ano", ano);
            return resultado;
        }
    }
    
    /**
     * Processar folha de um funcionário específico
     */
    public Map<String, Object> processarFolhaFuncionario(Long funcionarioId, int mes, int ano) {
        try {
            String url = FOLHA_URL + "/funcionario/" + funcionarioId + "?mes=" + mes + "&ano=" + ano;
            return restTemplate.postForObject(url, null, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao processar folha do funcionário: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Listar folhas por período
     */
    public List<Map<String, Object>> listarFolhasPorPeriodo(int mes, int ano) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                FOLHA_URL + "/periodo?mes=" + mes + "&ano=" + ano,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar folhas por período: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Buscar folha por ID
     */
    public Map<String, Object> buscarFolhaPorId(Long id) {
        try {
            return restTemplate.getForObject(FOLHA_URL + "/" + id, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar folha por ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Relatório completo da folha
     */
    public Map<String, Object> getRelatorioFolha(int mes, int ano) {
        try {
            return restTemplate.getForObject(FOLHA_URL + "/relatorio?mes=" + mes + "&ano=" + ano, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar relatório da folha: " + e.getMessage());
            Map<String, Object> relatorio = new HashMap<>();
            relatorio.put("mes", mes);
            relatorio.put("ano", ano);
            relatorio.put("totalFuncionarios", contarAtivos());
            relatorio.put("totalFolha", calcularFolhaMensal());
            return relatorio;
        }
    }
    
    /**
     * Estatísticas da folha
     */
    public Map<String, Object> getEstatisticasFolha(int mes, int ano) {
        try {
            return restTemplate.getForObject(FOLHA_URL + "/estatisticas?mes=" + mes + "&ano=" + ano, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar estatísticas da folha: " + e.getMessage());
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalFolha", calcularFolhaMensal());
            stats.put("mediaSalarial", calcularMediaSalarial());
            stats.put("totalFuncionarios", contarAtivos());
            return stats;
        }
    }
    
    /**
     * Adicionar comissão à folha
     */
    public void adicionarComissaoFolha(Long folhaId, Double valor) {
        try {
            String url = FOLHA_URL + "/" + folhaId + "/adicionar-comissao?valor=" + valor;
            restTemplate.patchForObject(url, null, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao adicionar comissão à folha: " + e.getMessage());
        }
    }
    
    /**
     * Marcar folha como paga
     */
    public void marcarFolhaPaga(Long folhaId) {
        try {
            restTemplate.patchForObject(FOLHA_URL + "/" + folhaId + "/marcar-pago", null, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao marcar folha como paga: " + e.getMessage());
        }
    }
    
    /**
     * Cancelar folha
     */
    public void cancelarFolha(Long folhaId) {
        try {
            restTemplate.patchForObject(FOLHA_URL + "/" + folhaId + "/cancelar", null, Map.class);
        } catch (Exception e) {
            System.err.println("Erro ao cancelar folha: " + e.getMessage());
        }
    }
    
    /**
     * Listar folhas pendentes para o sistema financeiro
     */
    public List<Map<String, Object>> listarFolhasPendentesFinanceiro(int mes, int ano) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                FOLHA_URL + "/financeiro/pendentes?mes=" + mes + "&ano=" + ano,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar folhas pendentes: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Confirmar pagamento (usado pelo sistema financeiro)
     */
    public void confirmarPagamentoFinanceiro(Long folhaId) {
        try {
            restTemplate.patchForObject(
                FOLHA_URL + "/financeiro/confirmar-pagamento/" + folhaId,
                null,
                Map.class
            );
        } catch (Exception e) {
            System.err.println("Erro ao confirmar pagamento: " + e.getMessage());
        }
    }
    
    // ==============================================
    // MÉTODOS AUXILIARES E ESTATÍSTICAS
    // ==============================================
    
    /**
     * Contar total de funcionários
     */
    public long contarTotal() {
        return listarTodos().size();
    }
    
    /**
     * Contar funcionários ativos
     */
    public long contarAtivos() {
        return listarAtivos().size();
    }
    
    /**
     * Contar funcionários inativos
     */
    public long contarInativos() {
        return listarInativos().size();
    }
    
    /**
     * Calcular folha mensal (salários base)
     */
    public double calcularFolhaMensal() {
        return listarAtivos().stream()
            .mapToDouble(f -> ((Number) f.getOrDefault("salarioBase", 0)).doubleValue())
            .sum();
    }
    
    /**
     * Calcular média salarial
     */
    public double calcularMediaSalarial() {
        List<Map<String, Object>> ativos = listarAtivos();
        if (ativos.isEmpty()) return 0.0;
        return ativos.stream()
            .mapToDouble(f -> ((Number) f.getOrDefault("salarioBase", 0)).doubleValue())
            .average()
            .orElse(0.0);
    }
    
    /**
     * Listar últimos N funcionários
     */
    public List<Map<String, Object>> listarUltimos(int quantidade) {
        List<Map<String, Object>> todos = listarTodos();
        int tamanho = todos.size();
        if (tamanho <= quantidade) {
            return todos;
        }
        return todos.subList(tamanho - quantidade, tamanho);
    }
    
    /**
     * Agrupar funcionários por departamento
     */
    public Map<String, List<Map<String, Object>>> agruparPorDepartamento() {
        List<Map<String, Object>> todos = listarTodos();
        return todos.stream()
            .filter(f -> f.get("departamento") != null)
            .collect(Collectors.groupingBy(
                f -> f.get("departamento").toString()
            ));
    }
    
    /**
     * Calcular totais por departamento
     */
    public Map<String, Double> calcularTotaisPorDepartamento() {
        Map<String, Double> totais = new HashMap<>();
        List<Map<String, Object>> ativos = listarAtivos();
        
        for (Map<String, Object> func : ativos) {
            String depto = (String) func.getOrDefault("departamento", "Outros");
            double salario = ((Number) func.getOrDefault("salarioBase", 0)).doubleValue();
            totais.put(depto, totais.getOrDefault(depto, 0.0) + salario);
        }
        
        return totais;
    }
}