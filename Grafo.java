import java.io.*;
import java.util.*;

public class Grafo {

    public static List<int[]> lerArestas(String caminho) throws IOException {
        List<int[]> arestas = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(caminho));
        String linha;
        while ((linha = br.readLine()) != null) {
            String[] partes = linha.trim().split(" ");
            int origem = Integer.parseInt(partes[0]);
            int destino = Integer.parseInt(partes[1]);
            arestas.add(new int[] { origem, destino });
        }
        br.close();
        return arestas;
    }

    public static Map<Integer, List<int[]>> lerArestasComPesos(String caminho) throws IOException {
        Map<Integer, List<int[]>> grafo = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(caminho));
        String linha;
        while ((linha = br.readLine()) != null) {
            String[] partes = linha.trim().split(" ");
            int origem = Integer.parseInt(partes[0]);
            int destino = Integer.parseInt(partes[1]);
            int peso = (int) Double.parseDouble(partes[2]);
            grafo.putIfAbsent(origem, new ArrayList<>());
            grafo.get(origem).add(new int[] { destino, peso });
        }
        br.close();
        return grafo;
    }

    public static Map<Integer, List<Integer>> grafoInvertido(List<int[]> arestas) {
        Map<Integer, List<Integer>> grafo = new HashMap<>();
        for (int[] a : arestas) {
            int origem = a[0], destino = a[1];
            grafo.computeIfAbsent(destino, k -> new ArrayList<>()).add(origem); // Invertido corretamente
        }
        return grafo;
    }

    public static int bfsDistancia(Map<Integer, List<Integer>> grafo, int origem, int destino) {
        Set<Integer> visitados = new HashSet<>();
        Queue<int[]> fila = new LinkedList<>();
        fila.add(new int[] { origem, 0 });
        visitados.add(origem);

        while (!fila.isEmpty()) {
            int[] atual = fila.poll();
            int no = atual[0];
            int dist = atual[1];
            if (no == destino)
                return dist;
            for (int vizinho : grafo.getOrDefault(no, new ArrayList<>())) {
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    fila.add(new int[] { vizinho, dist + 1 });
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    public static int bfs(Map<Integer, List<Integer>> grafo, int alvo) {
        Set<Integer> visitados = new HashSet<>();
        Queue<Integer> fila = new LinkedList<>();
        fila.add(alvo);
        visitados.add(alvo);

        while (!fila.isEmpty()) {
            int atual = fila.poll();
            for (int vizinho : grafo.getOrDefault(atual, new ArrayList<>())) {
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    fila.add(vizinho);
                }
            }
        }
        return visitados.size() - 1;
    }

    public static int[][] warshall(int n, List<int[]> arestas) {
        int[][] matriz = new int[n][n];
        Set<Integer> vertices = new HashSet<>();
        for (int[] a : arestas) {
            matriz[a[0] - 1][a[1] - 1] = 1;
            vertices.add(a[0]);
            vertices.add(a[1]);
        }
        for (int k = 0; k < n; k++) {
            if (vertices.contains(k + 1)) {
                for (int i = 0; i < n; i++) {
                    if (vertices.contains(i + 1)) {
                        for (int j = 0; j < n; j++) {
                            if (vertices.contains(j + 1)) {
                                if (matriz[i][k] == 1 && matriz[k][j] == 1) {
                                    matriz[i][j] = 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        return matriz;
    }

    public static Map<Integer, Double> dijkstra(Map<Integer, List<int[]>> grafo, int origem) {
        Map<Integer, Double> dist = new HashMap<>();
        for (int v : grafo.keySet()) {
            dist.put(v, Double.POSITIVE_INFINITY);
        }
        dist.put(origem, 0.0);

        PriorityQueue<int[]> fila = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
        fila.add(new int[] { origem, 0 });

        while (!fila.isEmpty()) {
            int[] atual = fila.poll();
            int no = atual[0];
            double custoAtual = dist.get(no);

            for (int[] viz : grafo.getOrDefault(no, new ArrayList<>())) {
                int destino = viz[0];
                double peso = viz[1];
                double novoCusto = custoAtual + peso;
                if (novoCusto < dist.getOrDefault(destino, Double.POSITIVE_INFINITY)) {
                    dist.put(destino, novoCusto);
                    fila.add(new int[] { destino, (int) novoCusto });
                }
            }
        }
        return dist;
    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        System.out.println("Lendo arestas do arquivo...");
        List<int[]> arestas = lerArestas("arquivo.txt");
        Map<Integer, List<Integer>> invertido = grafoInvertido(arestas);

        System.out.println("\n=== Top 2 mais alcançados questão 1 ===");
        Map<Integer, Integer> contagem = new HashMap<>();
        for (int i = 1; i <= 29; i++) {
            contagem.put(i, bfs(invertido, i));
        }
        contagem.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(2)
                .forEach(e -> System.out.println("Aluno " + e.getKey() + ": " + e.getValue() + " o alcançam"));

        System.out.println("\n=== Usando o BFS no grafo invertido questao 5  ===");
        System.out.print("\nDigite um vértice para saber quantos o alcançam: ");
        int alvo = in.nextInt();
        int total = bfs(invertido, alvo);
        System.out.println("Total que alcançam " + alvo + ": " + total);

        System.out.println("\n=== Fecho Transitivo questão 6 ===");
        int[][] fecho = warshall(29, arestas);
        System.out.print("Digite um vértice para saber quem ele alcança: ");
        int x = in.nextInt();
        System.out.print("Ele alcança: ");
        for (int j = 0; j < 29; j++) {
            if (fecho[x - 1][j] == 1) {
                System.out.print((j + 1) + " ");
            }
        }
        System.out.println();
        System.out.print("Digite um vértice para saber quem o alcança: ");
        int y = in.nextInt();
        System.out.print("É alcançado por: ");
        for (int i = 0; i < 29; i++) {
            if (fecho[i][y - 1] == 1) {
                System.out.print((i + 1) + " ");
            }
        }

        System.out.println("\n\n=== Questão 9: Centralidades ===");

        System.out.println("\n--- Centralidade de Influência Direta (grau de entrada no grafo invertido) ---");

        Map<Integer, Integer> influenciaDireta = new HashMap<>();

        // inicializa todos com 0
        for (int i = 1; i <= 29; i++) {
            influenciaDireta.put(i, 0);
        }

        // percorre o grafo invertido e conta quantos apontam para cada um
        for (List<Integer> lista : invertido.values()) {
            for (int destino : lista) {
                influenciaDireta.put(destino, influenciaDireta.get(destino) + 1);
            }
        }

        influenciaDireta.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> System.out.println(
                        "Aluno " + e.getKey() + ": é influenciado diretamente por " + e.getValue() + " alunos"));

        System.out.println("\n--- Centralidade de Aproximação ---");
        System.out.println("Os 5 alunos com as menores somas das distâncias mínimas até os demais alunos");
        Map<Integer, Integer> closeness = new HashMap<>();
        for (int i = 1; i <= 29; i++) {
            int soma = 0;
            boolean temDistancia = false;
            for (int j = 1; j <= 29; j++) {
                if (i != j) {
                    int d = bfsDistancia(invertido, i, j);
                    if (d != Integer.MAX_VALUE) {
                        soma += d;
                        temDistancia = true;
                    }
                }
            }
            if (temDistancia) {
                closeness.put(i, soma);
            }
        }
        closeness.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(29)
                .forEach(e -> System.out.println("Aluno " + e.getKey() + ": soma das distâncias = " + e.getValue()));

        System.out.println("\n\n=== Dijkstra com pesos questão 13 ===");
        System.out.println("Lendo arestas com peso do arquivo 'arquivo_pesos.txt'...");
        Map<Integer, List<int[]>> grafoPesado = lerArestasComPesos("arquivo_pesos.txt");

        System.out.print("Digite o vértice de origem: ");
        int origem = in.nextInt();
        Map<Integer, Double> dist = dijkstra(grafoPesado, origem);
        System.out.println("Distâncias a partir de " + origem + ":");
        for (Map.Entry<Integer, Double> e : dist.entrySet()) {
            System.out.println(origem + " → " + e.getKey() + " = " + e.getValue());
        }
    }
}
