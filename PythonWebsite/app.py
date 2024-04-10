import firebase_admin
from firebase_admin import credentials, db

# Initialize Firebase app with your credentials
cred = credentials.Certificate("yearproject-4a736-firebase-adminsdk-hdwrj-69bb4f7dae.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://yearproject-4a736-default-rtdb.firebaseio.com/'
})

# Get a reference to the database service
ref = db.reference('Nodes')  # 'Nodes' is the path to your nodes in the database

# Retrieve all nodes from the database
all_nodes = ref.get()

# Check if nodes exist
if all_nodes:
    # If all_nodes is a list
    if isinstance(all_nodes, list):
        # Loop through each node
        for index, node_data in enumerate(all_nodes):
            # Check if node_data is not None and has 'LocationX' and 'LocationY' coordinates
            if node_data and 'LocationX' in node_data and 'LocationY' in node_data:
                # Multiply the 'LocationX' and 'LocationY' coordinates by 2.63
                new_location_x = round(node_data['LocationX'] * 2.63)
                new_location_y = round(node_data['LocationY'] * 2.63)

                # Update the node with new coordinates
                ref.child(str(index)).update({
                    'LocationX': new_location_x,
                    'LocationY': new_location_y
                })
                print(f"Node at index {index} updated with new coordinates (LocationX={new_location_x}, LocationY={new_location_y})")
            elif node_data:
                print(f"Node at index {index} has incomplete data and was not updated.")
            else:
                print(f"Node at index {index} is None and was skipped.")
    # If all_nodes is a dictionary
    elif isinstance(all_nodes, dict):
        # Loop through each node
        for node_id, node_data in all_nodes.items():
            # Check if node_data is not None and has 'LocationX' and 'LocationY' coordinates
            if node_data and 'LocationX' in node_data and 'LocationY' in node_data:
                # Multiply the 'LocationX' and 'LocationY' coordinates by 2.63
                new_location_x = round(node_data['LocationX'] * 2.63)
                new_location_y = round(node_data['LocationY'] * 2.63)

                # Update the node with new coordinates
                ref.child(node_id).update({
                    'LocationX': new_location_x,
                    'LocationY': new_location_y
                })
                print(f"Node {node_id} updated with new coordinates (LocationX={new_location_x}, LocationY={new_location_y})")
            elif node_data:
                print(f"Node {node_id} has incomplete data and was not updated.")
            else:
                print(f"Node {node_id} is None and was skipped.")

    print("All nodes updated successfully!")
else:
    print("No nodes found in the database.")