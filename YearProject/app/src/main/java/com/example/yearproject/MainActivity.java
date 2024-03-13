package com.example.yearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    // TO DO :

    // 0. Try the program without the reverse function .. // Doesn't work ???

    // 1. ADD search // then ask for start point // to make start-end dynamic //Done

    // 2. Find how to put another images. //Done
    // 2.1. Add Buttons on the side to move thorough layers //Done
    // 2.2 Make the nodes related to the floor they are on //Done
    //      2.2.1 Add Nodes array +floor // Done
    //          2.2.1.1 Fully Convert the Program to array of Nodes instead of PointF[] // Done
    // 2.3 Put the Id on top of the nodes to make it easier to put them in place // Done
    // 2.4 Add Reverse Field for Connections // Done
        // 2.4.1 Read the field in the code and if not 1 then do not reverse. // Done

    // 3. Modify the code to send you to the stairs if you have to go down or up the stairs.
    // 3.1 Have to remember the way for floor 0/1/2
    //      3.1.1 Maybe keep the path for each floor individually // Made an array for each floor
    // 3.2 Maybe add stairs as a completely different type of Nodes
    //      3.2.1 Stairs might need a new field availability
    //          3.2.1.1 Availability --> one way system down one way system up or both ways
    //              3.2.1.2 Maybe just add this to all the nodes ???   <--- in the connection add the both or single
    // 3.3 Figure out how to make the algorithm choose the closest staircase
    //      3.3.1 Maybe just make the algorithm calculate the distance to each staircase
            //individually and choose the closest one to display

    // ---------------------------------------------------------------------------------------------------------\

    // 3.4 Add autofill into the search bar
    // 3.4.1 stop the algorithm from using the id ,use room nr and second name instead

    // 4. Make the Final Version of Images for all 3 floors
    // 4.1 Put the dots in place
    // 4.1.1 Add the person.ico / stairs-down / stairs-up to their place


    //Notes:
    // I changed from an array of PointF to an array of objects Node. Got the green nodes to appear only to the floor they are on . Still have to do the connections.

    //At the moment the algorithm is working only once gotta make it work 3 times , if the location is on another floor ,
    // go to that floor by fining the path to the stairs than go down and show the other path
    // Short: Have 3 algorithms for all 3 floors
    Graph connectionsGraph = null;
    Graph nodesGraph = null;

    static List<Integer> newpath;

    private EditText editText;
    private Button submitButton;
    private LinearLayout dynamicEditTextsLayout;
    private boolean isInputBoxAdded = false;
    private static String startlocation,endlocation;
    private Button buttonImage1, buttonImage2, buttonImage3;

    private int floor0[],floor1[],floor2[]; // <------ keep the path for each floor separated

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawingView drawingView = findViewById(R.id.drawingView); // Assuming the ID of your DrawingView is "drawingView"

        editText = findViewById(R.id.editText);
        submitButton = findViewById(R.id.submitButton);

        dynamicEditTextsLayout = findViewById(R.id.dynamicEditTextsLayout); // Initialize the layout

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    handleInput(userInput);
                    endlocation = userInput;
                    addNewEditText(); // send endlocation
                } else {
                    Toast.makeText(MainActivity.this, "Please enter the desired destination you want to get to", Toast.LENGTH_SHORT).show();
                }
            }
        }); //Go to submit button


        buttonImage1 = findViewById(R.id.buttonImage1);
        buttonImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.changeImage(R.drawable.gftest);
            }
        });
        buttonImage2 = findViewById(R.id.buttonImage2);
        buttonImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.changeImage(R.drawable.gftest2);
            }
        });
        buttonImage3 = findViewById(R.id.buttonImage3);
        buttonImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.changeImage(R.drawable.gftest3);
            }
        });

        /* -------------------------------------------------------------------------- */
        /*                              FireBase Database                             */
        /* -------------------------------------------------------------------------- */

        // Access the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Retrieve data from the specified branch ("Connection")
        database.getReference("Connection").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalSize = (int) dataSnapshot.getChildrenCount() + 1;

                connectionsGraph = new Graph(totalSize);
                System.out.println("TotalSize connections : " + totalSize);
                // Process the retrieved data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String start = snapshot.child("start").getValue(String.class);
                    String end = snapshot.child("end").getValue(String.class);
                    String weight = snapshot.child("weight").getValue(String.class);
                    int reversed = snapshot.child("reversed").getValue(Integer.class);

                    // Print the retrieved data (you can perform further processing here)
                    System.out.println("Start: " + Integer.parseInt(start) + ", End: " + Integer.parseInt(end) + ", Weight: " + Integer.parseInt(weight));
                    connectionsGraph.addEdge(Integer.parseInt(start), Integer.parseInt(end), Integer.parseInt(weight), reversed);
                }

                // Call A* algorithm
                //performAStarAlgorithm();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                System.out.println("Failed to read value: " + databaseError.toException());
            }
        });

        // Retrieve data from the specified branch ("Nodes")
        database.getReference("Nodes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalSize = (int) dataSnapshot.getChildrenCount();

                nodesGraph = new Graph(totalSize);
                System.out.println("TotalSize Nodes : " + totalSize);

                Node[] nodeCoordinates = new Node[totalSize];
                int index = 0;
                // Process the retrieved data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String nodeid = snapshot.getKey();
                    int floor = snapshot.child("Floor").getValue(Integer.class);
                    int locationX = snapshot.child("LocationX").getValue(Integer.class);
                    int locationY = snapshot.child("LocationY").getValue(Integer.class);
                    int roomNr = snapshot.child("RoomNr").getValue(Integer.class);
                    String secondName = snapshot.child("SecondName").getValue(String.class);

                    Node node = new Node(Integer.parseInt(nodeid),floor,locationX, locationY,roomNr,secondName);
                    nodeCoordinates[index] = node;
                    index++;

                    // Print the retrieved data (you can perform further processing here)
                    System.out.println("My ID IS : -->> " + nodeid +"Floor : " + floor + ", locationX: " + locationX + ", locationY: " + locationY + ", roomNr: " + roomNr + ", secondName: " + secondName);


                }

                // Pass node coordinates to DrawingView
                drawingView.setNodeCoordinates(nodeCoordinates);

                for(int x = 0 ; x < totalSize; x++)
                {
                    System.out.println("This is the print --> " + nodeCoordinates[x]);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                System.out.println("Failed to read value: " + databaseError.toException());
            }
        });
    }
    ///////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //Just an output to test if the inputted data is inputted
    private void handleInput(String input) {
        // Do something with the input here
        Toast.makeText(this, "Input: " + input, Toast.LENGTH_SHORT).show();
    }

    //Second field that pops out and asks for initial location
    private void addNewEditText() {
        if (!isInputBoxAdded) {
            // Create a new LinearLayout to hold EditText and Button horizontally
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);

            // Create EditText
            EditText newEditText = new EditText(this);
            LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            editTextParams.setMargins(0, 16, 8, 0); // Adjust margins as needed
            newEditText.setLayoutParams(editTextParams);
            newEditText.setHint("Enter Start Location");
            newEditText.setBackgroundResource(R.drawable.edit_text_background); // Reference the custom background
            newEditText.setTextColor(getResources().getColor(android.R.color.black));
            newEditText.setPadding(12, 8, 12, 8); // Adjust padding as needed

            // Create Submit Button for EditText
            Button newSubmitButton = new Button(this);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            buttonParams.setMargins(0, 8, 0, 0); // Adjust margins as needed
            newSubmitButton.setLayoutParams(buttonParams);
            newSubmitButton.setText("Submit");
            newSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle submission for this new EditText
                    String userInput = newEditText.getText().toString().trim();
                    if (!userInput.isEmpty()) {
                        handleInput(userInput);
                        // Hide the LinearLayout containing EditText and Button
                        linearLayout.setVisibility(View.GONE);
                        isInputBoxAdded = false; // Reset flag for future addition
                        startlocation = userInput;
                        performAStarAlgorithm();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter text", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Add EditText and Button to the LinearLayout
            linearLayout.addView(newEditText);
            linearLayout.addView(newSubmitButton);

            // Add the LinearLayout to the main layout
            dynamicEditTextsLayout.addView(linearLayout);

            isInputBoxAdded = true;
        }
    }

    /* -------------------------------------------------------------------------- */
    /*                           A* Algorithm Beginning                           */
    /*                           Creation of the Graphs                           */
    /* -------------------------------------------------------------------------- */
    private void performAStarAlgorithm() {

        // Print the original graph
        System.out.println("//Normal Graph//");
        connectionsGraph.printGraph();

        // Reverse the graph
        Graph reversedGraph = reverseGraph(connectionsGraph);
        System.out.println("//Reversed Graph//");
        reversedGraph.printGraph();

        // Join the original and reversed graphs
        Graph joinedGraph = joinGraphs(connectionsGraph, reversedGraph);
        System.out.println("//Joined Graphs//");
        joinedGraph.printGraph();

        // Define start and goal vertices
        //int startVertex = 1;
        //int goalVertex = 5;

        // Perform A* search
        System.out.println("A* Search from " + startlocation + " to " + endlocation + ":");


        /*                           Gotta do The magic in here                           */
        /* ------------------------------------------------------------------------------ */
        floor0 = null; // delete everything from the arrays whenever another search beggins
        floor1 = null;
        floor2 = null;
        //if(endlocation.floor) != current.floor
        // then get.endlocation.building.floor
        // then current node -> clossest floor till on the right floor
        //if(building == a)
        // buildinga();
        //buildinga()
        //{ initial xy --> each stair // get closest // display }
        aStarSearch(joinedGraph, Integer.valueOf(startlocation), Integer.valueOf(endlocation));

        // Update DrawingView with the new path
        DrawingView drawingView = findViewById(R.id.drawingView);
        drawingView.setPathForLine(newpath);
    }

    private static Graph reverseGraph(Graph connectionsGraph) {
        int totalVertices = connectionsGraph.vertices + 2;
        Graph reversedGraph = new Graph(totalVertices);

        // Reverse edges by swapping source and destination
        for (int i = 0; i < connectionsGraph.vertices; i++) {
            for (Edge edge : connectionsGraph.adjacencylist[i]) {
                if(edge.reversed == 1) // If said to be reversed reverse
                {
                    reversedGraph.addEdge(edge.destination, i, edge.weight, edge.reversed );
                }
            }
        }

        return reversedGraph;
    }

    private static Graph joinGraphs(Graph graph1, Graph graph2) {
        // Create a new graph with combined vertices
        int totalVertices = graph1.vertices + 2;
        Graph joinedGraph = new Graph(totalVertices);

        // Add edges from the first graph
        for (int i = 0; i < graph1.vertices; i++) {
            for (Edge edge : graph1.adjacencylist[i]) {
                joinedGraph.addEdge(i, edge.destination, edge.weight, edge.reversed);
            }
        }

        // Add edges from the second graph, adjusting node indices
        for (int i = 0; i < graph2.vertices; i++) {
            for (Edge edge : graph2.adjacencylist[i]) {
                joinedGraph.addEdge(i, edge.destination, edge.weight, edge.reversed);
            }
        }

        return joinedGraph;
    }

    /* -------------------------------------------------------------------------- */
    /*                                 A* Algorithm                               */
    /* -------------------------------------------------------------------------- */
    static class Edge {
        int source;
        int destination;
        int weight;
        int reversed;

        public Edge(int source, int destination, int weight, int reversed) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
            this.reversed = reversed;
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

        public void addEdge(int source, int destination, int weight, int reversed) {
            Edge edge = new Edge(source, destination, weight, reversed);
            adjacencylist[source].add(edge);
        }

        public void printGraph() {
            for (int i = 0; i < vertices; i++) {
                System.out.print("Vertex " + i + " is connected to: ");
                for (Edge edge : adjacencylist[i]) {
                    System.out.print(edge.destination + " (Weight: " + edge.weight + ") ");
                }
                System.out.println();
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

    private static void aStarSearch(Graph graph, int start, int goal) {
        PriorityQueue<AStarNode> priorityQueue = new PriorityQueue<>();
        boolean[] visited = new boolean[graph.vertices];

        priorityQueue.add(new AStarNode(start, 0, new ArrayList<>()));

        while (!priorityQueue.isEmpty()) {
            AStarNode current = priorityQueue.poll();

            if (current.vertex == goal) {
                // Call the Line Connector Draw in here !!!!
                newpath = current.path; // Assign the current Path to global path to send it into DrawingView
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
}
