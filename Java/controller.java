import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class FirebaseGraphReader {

    public static void main(String[] args) {
        initializeFirebaseApp(); // Initialize Firebase App

        // Specify the path to your Firebase Realtime Database
        String databaseUrl = "https://your-firebase-project-id.firebaseio.com";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(databaseUrl).getReference("/edges");

        // Read data from Firebase and build the weighted graph
        buildWeightedGraph(databaseReference);
    }

    private static void initializeFirebaseApp() {
        try {
            FileInputStream serviceAccount = new FileInputStream("path/to/your/firebase/serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://your-firebase-project-id.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void buildWeightedGraph(DatabaseReference databaseReference) {
        Map<String, Map<String, Integer>> edgesData = new HashMap<>();

        // Attach a listener to read the data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String startNode = snapshot.child("Start").getValue(String.class);
                    String endNode = snapshot.child("End").getValue(String.class);
                    int weight = snapshot.child("Weight").getValue(Integer.class);

                    edgesData.computeIfAbsent(startNode, k -> new HashMap<>()).put(endNode, weight);
                }

                // Build the weighted graph using the edges data
                WeightedGraph weightedGraph = new WeightedGraph(edgesData);
                weightedGraph.printGraph();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error reading data from Firebase: " + databaseError.getMessage());
            }
        });
    }

    // WeightedGraph class to represent the graph
    static class WeightedGraph {
        private final Map<String, Map<String, Integer>> edges;

        public WeightedGraph(Map<String, Map<String, Integer>> edges) {
            this.edges = edges;
        }

        public void printGraph() {
            for (Map.Entry<String, Map<String, Integer>> entry : edges.entrySet()) {
                String startNode = entry.getKey();
                Map<String, Integer> neighbors = entry.getValue();

                for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                    String endNode = neighbor.getKey();
                    int weight = neighbor.getValue();

                    System.out.println("Edge: " + startNode + " -> " + endNode + " (Weight: " + weight + ")");
                }
            }
        }
    }
}