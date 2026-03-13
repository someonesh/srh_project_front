package srh_project.srh_project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import srh_project.srh_project.service.FuncionarioService;

@Controller
public class HomeController {
    
    @Autowired
    private FuncionarioService funcionarioService;
    
    // ==============================================
    // PÁGINAS PRINCIPAIS
    // ==============================================
    
    @GetMapping("/")
    public String home(Model model) {
        try {
            // Buscar dados da API
            List<Map<String, Object>> funcionarios = funcionarioService.listarTodos();
            List<Map<String, Object>> ativos = funcionarioService.listarAtivos();
            
            // USAR FILTROS LOCAIS (não chamam a API diretamente)
            List<Map<String, Object>> vendedores = funcionarioService.filtrarVendedores();
            List<Map<String, Object>> aniversariantes = funcionarioService.filtrarAniversariantes();
            
            // Calcular estatísticas
            long total = funcionarios.size();
            long totalAtivos = ativos.size();
            double folhaMensal = ativos.stream()
                .mapToDouble(f -> ((Number) f.getOrDefault("salarioBase", 0)).doubleValue())
                .sum();
            
            // Adicionar atributos ao modelo
            model.addAttribute("totalFuncionarios", total);
            model.addAttribute("funcionariosAtivos", totalAtivos);
            model.addAttribute("funcionariosInativos", total - totalAtivos);
            model.addAttribute("folhaMensal", folhaMensal);
            model.addAttribute("totalVendedores", vendedores.size());
            
            // Últimos 5 funcionários
            model.addAttribute("ultimosFuncionarios", 
                funcionarios.stream().limit(5).collect(Collectors.toList()));
            
            // Aniversariantes do mês
            model.addAttribute("aniversariantes", aniversariantes);
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados da home: " + e.getMessage());
            e.printStackTrace();
            
            // Valores padrão em caso de erro
            model.addAttribute("totalFuncionarios", 0);
            model.addAttribute("funcionariosAtivos", 0);
            model.addAttribute("funcionariosInativos", 0);
            model.addAttribute("folhaMensal", 0.0);
            model.addAttribute("totalVendedores", 0);
            model.addAttribute("ultimosFuncionarios", List.of());
            model.addAttribute("aniversariantes", List.of());
        }
        
        return "home";
    }
    
    @GetMapping("/funcionarios")
    public String funcionarios(Model model) {
        try {
            List<Map<String, Object>> funcionarios = funcionarioService.listarTodos();
            List<Map<String, Object>> ativos = funcionarioService.listarAtivos();
            List<Map<String, Object>> inativos = funcionarioService.listarInativos();
            
            model.addAttribute("funcionarios", funcionarios);
            model.addAttribute("ativos", ativos.size());
            model.addAttribute("inativos", inativos.size());
            model.addAttribute("total", funcionarios.size());
            
        } catch (Exception e) {
            model.addAttribute("funcionarios", List.of());
            model.addAttribute("ativos", 0);
            model.addAttribute("inativos", 0);
            model.addAttribute("total", 0);
            model.addAttribute("erro", "Erro ao carregar funcionários: " + e.getMessage());
        }
        return "funcionarios";
    }
    
    @GetMapping("/vendedores")
    public String vendedores(Model model) {
        try {
            List<Map<String, Object>> vendedores = funcionarioService.filtrarVendedores();
            Map<String, Object> estatisticas = funcionarioService.getEstatisticasVendedores();
            
            model.addAttribute("vendedores", vendedores);
            model.addAttribute("totalVendedores", vendedores.size());
            model.addAttribute("estatisticas", estatisticas);
            
        } catch (Exception e) {
            model.addAttribute("vendedores", List.of());
            model.addAttribute("totalVendedores", 0);
            model.addAttribute("estatisticas", new HashMap<>());
            model.addAttribute("erro", "Erro ao carregar vendedores: " + e.getMessage());
        }
        return "vendedores";
    }
    
    @GetMapping("/folha-pagamento")
    public String folhaPagamento(Model model) {
        try {
            List<Map<String, Object>> ativos = funcionarioService.listarAtivos();
            List<Map<String, Object>> vendedores = funcionarioService.filtrarVendedores();
            
            // Calcular totais
            double totalFolha = ativos.stream()
                .mapToDouble(f -> ((Number) f.getOrDefault("salarioBase", 0)).doubleValue())
                .sum();
            
            double totalComissoes = vendedores.stream()
                .mapToDouble(v -> {
                    double vendas = ((Number) v.getOrDefault("vendasMes", 0)).doubleValue();
                    double percentual = ((Number) v.getOrDefault("percentualComissao", 5)).doubleValue();
                    return vendas * (percentual / 100);
                })
                .sum();
            
            double mediaSalarial = ativos.size() > 0 ? totalFolha / ativos.size() : 0;
            double maiorSalario = ativos.stream()
                .mapToDouble(f -> ((Number) f.getOrDefault("salarioBase", 0)).doubleValue())
                .max()
                .orElse(0);
            
            model.addAttribute("ativos", ativos);
            model.addAttribute("vendedores", vendedores);
            model.addAttribute("totalFolha", totalFolha);
            model.addAttribute("totalComissoes", totalComissoes);
            model.addAttribute("mediaSalarial", mediaSalarial);
            model.addAttribute("maiorSalario", maiorSalario);
            model.addAttribute("totalAtivos", ativos.size());
            
        } catch (Exception e) {
            model.addAttribute("ativos", List.of());
            model.addAttribute("vendedores", List.of());
            model.addAttribute("totalFolha", 0.0);
            model.addAttribute("totalComissoes", 0.0);
            model.addAttribute("mediaSalarial", 0.0);
            model.addAttribute("maiorSalario", 0.0);
            model.addAttribute("totalAtivos", 0);
            model.addAttribute("erro", "Erro ao carregar folha: " + e.getMessage());
        }
        return "folha-pagamento";
    }
    
    @GetMapping("/ferias")
    public String ferias(Model model) {
        try {
            List<Map<String, Object>> funcionarios = funcionarioService.listarTodos();
            
            // Simular dados de férias (em produção viriam da API)
            List<Map<String, Object>> feriasPendentes = List.of(
                Map.of("funcionario", "João Silva", "inicio", "10/03/2026", "fim", "24/03/2026", "status", "pendente"),
                Map.of("funcionario", "Maria Santos", "inicio", "15/03/2026", "fim", "29/03/2026", "status", "aprovada")
            );
            
            model.addAttribute("funcionarios", funcionarios);
            model.addAttribute("feriasPendentes", feriasPendentes);
            model.addAttribute("totalPendentes", 1);
            model.addAttribute("totalAprovadas", 1);
            
        } catch (Exception e) {
            model.addAttribute("funcionarios", List.of());
            model.addAttribute("feriasPendentes", List.of());
            model.addAttribute("totalPendentes", 0);
            model.addAttribute("totalAprovadas", 0);
        }
        return "ferias";
    }
    
    @GetMapping("/relatorios")
    public String relatorios(Model model) {
        try {
            List<Map<String, Object>> funcionarios = funcionarioService.listarTodos();
            List<Map<String, Object>> ativos = funcionarioService.listarAtivos();
            List<Map<String, Object>> vendedores = funcionarioService.filtrarVendedores();
            
            // Estatísticas gerais
            long total = funcionarios.size();
            long ativosCount = ativos.size();
            double folhaMensal = ativos.stream()
                .mapToDouble(f -> ((Number) f.getOrDefault("salarioBase", 0)).doubleValue())
                .sum();
            
            // Contagem por departamento
            Map<String, Long> deptCount = new HashMap<>();
            funcionarios.stream()
                .filter(f -> f.get("departamento") != null)
                .forEach(f -> {
                    String depto = f.get("departamento").toString();
                    deptCount.put(depto, deptCount.getOrDefault(depto, 0L) + 1);
                });
            
            // Totais por departamento
            Map<String, Double> deptTotals = new HashMap<>();
            ativos.stream()
                .filter(f -> f.get("departamento") != null)
                .forEach(f -> {
                    String depto = f.get("departamento").toString();
                    double salario = ((Number) f.getOrDefault("salarioBase", 0)).doubleValue();
                    deptTotals.put(depto, deptTotals.getOrDefault(depto, 0.0) + salario);
                });
            
            model.addAttribute("totalFuncionarios", total);
            model.addAttribute("ativos", ativosCount);
            model.addAttribute("inativos", total - ativosCount);
            model.addAttribute("folhaMensal", folhaMensal);
            model.addAttribute("departamentos", deptCount);
            model.addAttribute("deptTotals", deptTotals);
            model.addAttribute("totalVendedores", vendedores.size());
            
        } catch (Exception e) {
            model.addAttribute("totalFuncionarios", 0);
            model.addAttribute("ativos", 0);
            model.addAttribute("inativos", 0);
            model.addAttribute("folhaMensal", 0.0);
            model.addAttribute("departamentos", Map.of());
            model.addAttribute("deptTotals", Map.of());
            model.addAttribute("totalVendedores", 0);
            model.addAttribute("erro", "Erro ao carregar relatórios: " + e.getMessage());
        }
        return "relatorios";
    }
    
    @GetMapping("/sobre")
    public String sobre(Model model) {
        model.addAttribute("versao", "1.0.0");
        model.addAttribute("ano", java.time.Year.now().getValue());
        model.addAttribute("tecnologias", List.of(
            "Spring Boot 3.2.3",
            "Thymeleaf",
            "Bootstrap 5",
            "MySQL",
            "REST API"
        ));
        return "sobre";
    }
    
    // ==============================================
    // ENDPOINTS AUXILIARES (para testes)
    // ==============================================
    
    @GetMapping("/api-status")
    @ResponseBody
    public Map<String, Object> apiStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            List<Map<String, Object>> funcionarios = funcionarioService.listarTodos();
            status.put("status", "online");
            status.put("funcionarios", funcionarios.size());
            status.put("api_url", "https://srh-api.onrender.com/api");
            status.put("mensagem", "API conectada com sucesso!");
        } catch (Exception e) {
            status.put("status", "offline");
            status.put("erro", e.getMessage());
            status.put("api_url", "https://srh-api.onrender.com/api");
        }
        return status;
    }
    
    @GetMapping("/health")
    @ResponseBody
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "timestamp", java.time.LocalDateTime.now().toString(),
            "version", "1.0.0"
        );
    }
}