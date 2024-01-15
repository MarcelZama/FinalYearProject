
// The A* algorithm is commonly used for pathfinding in graphs, and it can be applied to both weighted and unweighted graphs.
// The choice between using a weighted graph or an unweighted graph depends on the characteristics of the problem you are trying to solve.

// Weighted Graphs:

// When to Use: If your application involves different costs or distances between nodes (vertices), you should use a weighted graph.
//The weights assigned to the edges represent the cost or distance between nodes.
// Example: In a map or grid-based scenario, where each edge has a different travel cost (distance, time, etc.), using a weighted graph is appropriate.
// Implementation: Each edge in the graph has an associated weight, and the A* algorithm considers both the cost to reach a node and a heuristic estimate of the cost to reach the goal.



import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.text.html.ImageView;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;


public class test{
    static class Edge {
        int source;
        int destination;
        int weight;

        public Edge(int source, int destination, int weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }
    }

    static class Graph {
        int vertices;
        LinkedList<Edge>[] adjacencylist;

        Graph(int vertices) {
            this.vertices = vertices;
            adjacencylist = new LinkedList[vertices];
            for (int i = 0; i < vertices; i++) {
                adjacencylist[i] = new LinkedList<>();
            }
        }

        public void addEdge(int source, int destination, int weight) {
            Edge edge = new Edge(source, destination, weight);
            adjacencylist[source].addFirst(edge);
        }

        public void printGraph() {
            for (int i = 0; i < vertices; i++) {
                LinkedList<Edge> list = adjacencylist[i];
                for (int j = 0; j < list.size(); j++) {
                    System.out.println("vertex - " + i + " is connected to " +
                            list.get(j).destination + " with weight " + list.get(j).weight);
                }
            }
        }
    }

    static class AStarNode implements Comparable<AStarNode> {
        int vertex;
        int cost;
        List<Integer> path;

        public AStarNode(int vertex, int cost, List<Integer> path) {
            this.vertex = vertex;
            this.cost = cost;
            this.path = new ArrayList<>(path);
            this.path.add(vertex);
        }

        @Override
        public int compareTo(AStarNode other) {
            return Integer.compare(this.cost, other.cost);
        }
    }

    public static void aStarSearch(Graph graph, int start, int goal) {
        PriorityQueue<AStarNode> priorityQueue = new PriorityQueue<>();
        boolean[] visited = new boolean[graph.vertices];

        priorityQueue.add(new AStarNode(start, 0, new ArrayList<>()));

        while (!priorityQueue.isEmpty()) {
            AStarNode current = priorityQueue.poll();

            if (current.vertex == goal) {
                System.out.println("Path from " + start + " to " + goal + ": " + current.path);
                System.out.println("Total Cost: " + current.cost);
                return;
            }

            if (!visited[current.vertex]) {
                visited[current.vertex] = true;

                for (Edge neighbor : graph.adjacencylist[current.vertex]) {
                    if (!visited[neighbor.destination]) {
                        int newCost = current.cost + neighbor.weight;
                        priorityQueue.add(new AStarNode(neighbor.destination, newCost, current.path));
                    }
                }
            }
        }

        System.out.println("Goal " + goal + " not reachable from start " + start);
    }

    public static void main(String[] args) {

        //map assign 0 to other things

        int vertices = 6;
        Graph graph = new Graph(vertices);
        graph.addEdge(0, 1, 4); 
        graph.addEdge(0, 2, 3);
        //graph.addEdge(0,4,4);
        graph.addEdge(1, 3, 2);
        graph.addEdge(1, 2, 5);
        graph.addEdge(2, 3, 7);
        graph.addEdge(3, 4, 2);
        graph.addEdge(4, 0, 4); // ?
        graph.addEdge(4, 1, 4);
        graph.addEdge(4, 5, 6);

        // int y2 =0;
        // for(int x1 = 0;x1< 7;x1++)
        // {
        //     y2 = x1+1;
        //     graph.addEdge(x1, y2, 11);
        // }

        graph.printGraph();


        //find 
        //if library = 70
        //if canteen = 20
        //if gym = 10
        int startVertex = 3;
        int goalVertex = 0;

        System.out.println("A* Search from " + startVertex + " to " + goalVertex + ":");
        aStarSearch(graph, startVertex, goalVertex);

        /////////////////////////////Output the Image
            String imagePath = "GFTest.png";  // Adjust the path accordingly

        try {
            // Read the image file
            BufferedImage image = ImageIO.read(new File(imagePath));

           // Change the color of a specific pixel (e.g., pixel at coordinates x=50, y=50)
           int x;
           int y;
           int newColorRGB = Color.RED.getRGB(); // Change this to the desired color
           for(int i=0;i<=50; i++)
           {
                x=i;
                y=i;
                image.setRGB(x, y, newColorRGB);
           }
           //image.setRGB(x, y, newColorRGB);

            // Save the modified image
            String outputImagePath = "modified_image.jpg";
            ImageIO.write(image, "jpg", new File(outputImagePath));

            // Create a JFrame to display the image
            JFrame frame = new JFrame("Image Display");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create a JLabel with the image icon
            ImageIcon icon = new ImageIcon(image);
            JLabel label = new JLabel(icon);

            // Add the JLabel to the JFrame
            frame.getContentPane().add(label, BorderLayout.CENTER);

            // Set JFrame properties
            frame.pack();
            frame.setLocationRelativeTo(null); // Center the frame
            frame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
