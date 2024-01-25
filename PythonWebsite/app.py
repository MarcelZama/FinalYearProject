from flask import Flask, render_template, request, redirect
import firebase_admin
from firebase_admin import credentials, db
from flask import redirect
import sys
from flask import jsonify

app = Flask(__name__)

# Initialize Firebase with your credentials
cred = credentials.Certificate("yearproject-4a736-firebase-adminsdk-hdwrj-69bb4f7dae.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://yearproject-4a736-default-rtdb.firebaseio.com/'
})

#/* -------------------------------------------------------------------------- */#
#/*                                 Nodes Data                                 */#
#/* -------------------------------------------------------------------------- */#

# Define the get_last_node_index() function
def get_last_node_index():
    nodes_data = db.reference('Nodes').get()

    if nodes_data is not None and isinstance(nodes_data, dict):
        # Get the keys (node names) and convert them to integers
        node_indices = [int(key) for key in nodes_data.keys()]

        # Return the maximum index
        return max(node_indices, default=0)
    else:
        return 0

# Define a route to render the main page
@app.route('/')
def index():
    # Read data from Firebase (assuming you have a 'Nodes' node)
    nodes_data = db.reference('Nodes').get()

    # Check if 'Nodes' is not None
    if nodes_data is not None:
        if isinstance(nodes_data, list):
            # Convert the list to a dictionary with index as key
            nodes_data = {str(index): node for index, node in enumerate(nodes_data)}

        return render_template('index.html', nodes_data=nodes_data)
    else:
        # If 'Nodes' is None, pass an empty dictionary to the template
        return render_template('index.html', nodes_data={})

# Define a route to handle form submissions
@app.route('/submit', methods=['POST'])
def submit_form():
    # Get the last node index directly from Firebase
    nodes_data = db.reference('Nodes').get()
    
    if nodes_data:
        # Convert the list to a dictionary with indices as keys
        nodes_dict = {str(index + 1): node for index, node in enumerate(nodes_data)}
        last_node_index = max([int(key) for key in nodes_dict.keys()], default=0)
    else:
        last_node_index = 0

    # Create a new node with an index one higher than the last one
    new_node_name = str(last_node_index)

    # Extract form data from the submitted form
    floor = request.form.get('floor')
    location_x = request.form.get('location_x')
    location_y = request.form.get('location_y')
    room_nr = request.form.get('room_nr')
    second_name = request.form.get('second_name')

    # Create a new node in Firebase with the submitted data
    db.reference('Nodes').child(new_node_name).set({
        'Floor': floor,
        'LocationX': location_x,
        'LocationY': location_y,
        'RoomNr': room_nr,
        'SecondName': second_name
    })

    # Redirect to the main page or any other appropriate URL
    return redirect('/')

# Define a route to handle node creation
@app.route('/create', methods=['POST'])
def create_node():
      # Get the last node index directly from Firebase
    last_node_index = max([int(key) for key in db.reference('Nodes').get() or {}], default=0)

    print("last nodeid is " + last_node_index)
    sys.stdout.write("last nodeid is " + last_node_index)
    
    # Create a new node with an index one higher than the last one
    new_node_name = str(last_node_index + 1)

    # Extract other fields from the form data
    floor = request.form.get('floor')
    location_x = request.form.get('location_x')
    location_y = request.form.get('location_y')
    room_nr = request.form.get('room_nr')
    second_name = request.form.get('second_name')

    # Create a new node in Firebase with the updated name
    db.reference('Nodes').child(new_node_name).set({
        'Floor': floor,
        'LocationX': location_x,
        'LocationY': location_y,
        'RoomNr': room_nr,
        'SecondName': second_name
    })
    return redirect('/')

# Define a route to handle node updates
@app.route('/update/<string:node_id>', methods=['POST'])
def update_node(node_id):
    # Extract form data from the submitted form
    floor = request.form.get('floor')
    location_x = request.form.get('location_x')
    location_y = request.form.get('location_y')
    room_nr = request.form.get('room_nr')
    second_name = request.form.get('second_name')

    # Update the existing node with the submitted data
    db.reference('Nodes').child(node_id).update({
        'Floor': floor,
        'LocationX': location_x,
        'LocationY': location_y,
        'RoomNr': room_nr,
        'SecondName': second_name
    })

    # Redirect to the main page
    return redirect('/')

# Define a route to handle node deletion
@app.route('/delete/<int:node_id>', methods=['POST'])
def delete_node(node_id):
    # Update the data in the Firebase database to remove the node with the specified NodeID
    db.reference('Nodes').child(str(node_id)).delete()

    return redirect('/')

#/* -------------------------------------------------------------------------- */#
#/*                                 Connection                                 */#
#/* -------------------------------------------------------------------------- */#


if __name__ == '__main__':
    app.run(debug=True)
