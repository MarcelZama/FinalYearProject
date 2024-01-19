
// The A* algorithm is commonly used for pathfinding in graphs, and it can be applied to both weighted and unweighted graphs.
// The choice between using a weighted graph or an unweighted graph depends on the characteristics of the problem you are trying to solve.

// Weighted Graphs:

// When to Use: If your application involves different costs or distances between nodes (vertices), you should use a weighted graph.
//The weights assigned to the edges represent the cost or distance between nodes.
// Example: In a map or grid-based scenario, where each edge has a different travel cost (distance, time, etc.), using a weighted graph is appropriate.
// Implementation: Each edge in the graph has an associated weight, and the A* algorithm considers both the cost to reach a node and a heuristic estimate of the cost to reach the goal.


//TO DO :
// 1. Add a link to a method inside the a* algorith that will draw the path on the map
// 2. Move the display from main to draw it ,where the loop will be 
// 3. // Done // Check if the graph points are not exeding the size, if they are just exit , or give an error that they exit the size 
// 4. Automate the points gathering 
// 5. // Done // Finish the A* algorithm to work in reverse too not only one direction
    //<-- understand why it dose not work with multiple nodes in reverse only with single 


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
        int locationX;
        int locationY;
        Edge next;
        Edge prev;
    
        public Edge(int source, int destination, int weight, int locationX, int locationY) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
            this.locationX = locationX;
            this.locationY = locationY;
            this.next = null;
            this.prev = null;
        }
    }
    

    static class Graph {
        int vertices;
        int verticescount = 0;
        Edge[] adjacencylist;

        Graph(int vertices) { // points
            this.vertices = vertices;
            adjacencylist = new Edge[vertices];
            for (int i = 0; i < vertices; i++) {
                adjacencylist[i] = null;
            }
        }

        public void addEdge(int source, int destination, int weight, int locationX, int locationY) {
            if (verticescount < vertices) {
                Edge edge = new Edge(source, destination, weight, locationX, locationY);
                if (adjacencylist[source] == null) {
                    adjacencylist[source] = edge;
                } else {
                    Edge last = adjacencylist[source];
                    while (last.next != null) {
                        last = last.next;
                    }
                    last.next = edge;
                    edge.prev = last;
                }
                ++verticescount;
            }
        }
        

        public void printGraph() {
            for (int i = 0; i < vertices; i++) {
                Edge current = adjacencylist[i];
                while (current != null) {
                    System.out.println("vertex - " + i + " is connected to " + current.destination + " with weight "
                            + current.weight);
                    current = current.next;
                }
            }
        }
    }

    static class AStarNode implements Comparable<AStarNode> {
        int vertex;
        int cost;
        List<Integer> path;
        boolean reverse; // Added field to indicate reverse traversal
    
        public AStarNode(int vertex, int cost, List<Integer> path, boolean reverse) {
            this.vertex = vertex;
            this.cost = cost;
            this.path = new ArrayList<>(path);
            this.path.add(vertex);
            this.reverse = reverse;
        }
    
        @Override
        public int compareTo(AStarNode other) {
            return Integer.compare(this.cost, other.cost);
        }
    }
    

    // public static void aStarSearchReverse(Graph graph, int start, int goal, BufferedImage image) {
    //     PriorityQueue<AStarNode> priorityQueue = new PriorityQueue<>();
    //     boolean[] visited = new boolean[graph.vertices];
    
    //     priorityQueue.add(new AStarNode(start, 0, new ArrayList<>()));
    
    //     while (!priorityQueue.isEmpty()) {
    //         AStarNode current = priorityQueue.poll();
    
    //         if (current.vertex == goal) {
    //             drawit(graph, image); //// <<<<<-----------
    //             System.out.println("Path from " + start + " to " + goal + ": " + current.path);
    //             System.out.println("Total Cost: " + current.cost);
    //             return;
    //         }
    
    //         if (!visited[current.vertex]) {
    //             visited[current.vertex] = true;
    
    //             Edge edge = graph.adjacencylist[current.vertex];
    
    //             while (edge != null) {
    //                 int neighborVertex = edge.destination;
    //                 if (!visited[neighborVertex]) {
    //                     int newCost = current.cost + edge.weight;
    //                     priorityQueue.add(new AStarNode(neighborVertex, newCost, current.path));
    //                 }
    //                 edge = edge.prev; // Traverse in the reverse direction
    //             }
    //         }
    //     }
    
    //     System.out.println("Goal " + goal + " not reachable from start " + start);
    // }
    

    // public static void aStarSearch(Graph graph, int start, int goal,BufferedImage image) {
    //     PriorityQueue<AStarNode> priorityQueue = new PriorityQueue<>();
    //     boolean[] visited = new boolean[graph.vertices];

    //     priorityQueue.add(new AStarNode(start, 0, new ArrayList<>()));

    //     while (!priorityQueue.isEmpty()) {
    //         AStarNode current = priorityQueue.poll();

    //         if (current.vertex == goal) {
    //             drawit(graph, image); //// <<<<<-----------
    //             System.out.println("Path from " + start + " to " + goal + ": " + current.path);
    //             System.out.println("Total Cost: " + current.cost);
    //             return;
    //         }

    //         if (!visited[current.vertex]) {
    //             visited[current.vertex] = true;
            
    //             Edge neighbor = graph.adjacencylist[current.vertex];

    //             while (neighbor != null) {
    //                 if (!visited[neighbor.destination]) {
    //                     int newCost = current.cost + neighbor.weight;
    //                     priorityQueue.add(new AStarNode(neighbor.destination, newCost, current.path));
    //                 }
    //                 neighbor = neighbor.next;
    //             }
    //         }
    //     }
    //         //if didntwork = 1
    //         aStarSearchReverse(graph,start,goal,image);
    // }

    public static void aStarSearch(Graph graph, int start, int goal, BufferedImage image) {
        aStarSearchInternal(graph, start, goal, image, false);
    }
    
    private static void aStarSearchInternal(Graph graph, int start, int goal, BufferedImage image, boolean reverse) {
        PriorityQueue<AStarNode> priorityQueue = new PriorityQueue<>();
        boolean[] visited = new boolean[graph.vertices];
    
        priorityQueue.add(new AStarNode(start, 0, new ArrayList<>(), reverse));
    
        while (!priorityQueue.isEmpty()) {
            AStarNode current = priorityQueue.poll();
    
            if (current.vertex == goal) {
                drawit(graph, image);
                System.out.println("Path from " + start + " to " + goal + ": " + current.path);
                System.out.println("Total Cost: " + current.cost);
                return;
            }
    
            if (!visited[current.vertex]) {
                visited[current.vertex] = true;
    
                Edge neighbor = reverse ? graph.adjacencylist[current.vertex].prev : graph.adjacencylist[current.vertex];
    
                while (neighbor != null) {
                    int neighborVertex = reverse ? neighbor.source : neighbor.destination; // Fix here
                    if (!visited[neighborVertex]) {
                        int newCost = current.cost + neighbor.weight;
                        priorityQueue.add(new AStarNode(neighborVertex, newCost, current.path, reverse));
                    }
                    neighbor = reverse ? neighbor.prev : neighbor.next;
                }
            }
        }
    
        System.out.println("Goal " + goal + " not reachable from start " + start);
    }        

    public static void drawit(Graph graph,BufferedImage image)
    {
        try {
            int newColorRGB = Color.YELLOW.getRGB(); // Change this to the desired color

            for (int i = 0; i < graph.vertices; i++) {
                Edge edge = graph.adjacencylist[i];
    
                while (edge != null) {
                    // Assuming nodes have coordinates x1, y1 and x2, y2
                    int x1 = i * 50;               // Adjust as needed
                    int y1 = 500;                   // Adjust as needed
                    int x2 = edge.destination * 50; // Adjust as needed
                    int y2 = 500;                   // Adjust as needed
    
                    drawEdge(image, x1, y1, x2, y2, newColorRGB);
                    edge = edge.next;
                }
            }

            // Save the modified image
            String outputImagePath = "modified_image.jpg";
            ImageIO.write(image, "jpg", new File(outputImagePath));

            // Create a JFrame to display the image
            JFrame frame = new JFrame("Marcel Zama College Maze resolver");
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

    public static void drawEdge(BufferedImage image, int x1, int y1, int x2, int y2, int colorRGB) {
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(new Color(colorRGB));
        g2d.drawLine(x1, y1, x2, y2);
        g2d.dispose();
        System.out.println("I am being called!");
    }

    public static void main(String[] args) {
        String imagePath = "/Users/marcis578/Documents/University/Project/Java/GFTest.png";


        int vertices = 6;
        Graph graph = new Graph(vertices);
        graph.addEdge(0, 1, 4,450,150); //1
                //graph.addEdge(3, 0, 4,450,150); //1

        graph.addEdge(0, 2, 3,150,510); //2
        //graph.addEdge(0,4,4);
        graph.addEdge(1, 3, 2,250,150); //3
        graph.addEdge(1, 2, 5,250,150); //4
        graph.addEdge(2, 3, 7,550,150); //5
        graph.addEdge(3, 4, 2,450,150); //6
        graph.addEdge(4, 0, 4,350,150); // ---
        graph.addEdge(4, 1, 4,250,150);

        graph.printGraph();


        int startVertex = 3;
        int goalVertex = 0;

        System.out.println("A* Search from " + startVertex + " to " + goalVertex + " :");
        // aStarSearch(graph, startVertex, goalVertex,image);
        try{
            BufferedImage image = ImageIO.read(new File(imagePath));
            aStarSearch(graph, startVertex, goalVertex,image);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
