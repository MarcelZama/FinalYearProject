from flask import Flask, render_template, request, redirect, session
import firebase_admin
from firebase_admin import credentials, db
from flask import redirect
import sys
from flask import jsonify
import secrets

app = Flask(__name__)
app.secret_key = secrets.token_urlsafe(16)

# Initialize Firebase with your credentials
cred = credentials.Certificate("yearproject-4a736-firebase-adminsdk-hdwrj-69bb4f7dae.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://yearproject-4a736-default-rtdb.firebaseio.com/'
})

atstart = 0

@app.route('/')
def first_page_select():
    if atstart == 0:
        return redirect('/login')
    else:
        return redirect('/nodes')
        return redirect('/connections')

def is_logged_in():
    return session.get('logged_in', False)

# Use the before_request decorator to check if the user is logged in before each request
@app.before_request
def before_request():
    if not is_logged_in() and request.endpoint not in ['login', 'first_page_select']:
        return redirect('/login')
    
#/* -------------------------------------------------------------------------- */#
#/*                                    LogIn                                   */#
#/* -------------------------------------------------------------------------- */#
    
# Define a route for the login page
@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form.get('username', '')
        password = request.form.get('password', '')

        # Check against Firebase credentials
        if check_credentials(username, password):
            session['logged_in'] = True
            return redirect('/nodes')  # Redirect to the nodes page after successful login
        else:
            return render_template('login.html', error='Invalid username or password')

    return render_template('login.html', error=None)

# Function to check credentials against Firebase
def check_credentials(username, password):
    # Read data from Firebase (assuming you have a 'Credentials' node)
    credentials_data = db.reference('Credentials').get()

    if credentials_data and 'LogIn' in credentials_data and 'Password' in credentials_data:
        stored_login = credentials_data['LogIn'].strip()
        stored_password = credentials_data['Password'].strip()

        if stored_login == username and stored_password == password:
            print("Authentication successful!")
            return True
        else:
            print("Incorrect login or password.")
    else:
        print("Credentials not found in the database.")

    return False

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
@app.route('/nodes')
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
    return redirect('/nodes')

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
    return redirect('/nodes')

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
    return redirect('/nodes')

# Define a route to handle node deletion
@app.route('/delete/<int:node_id>', methods=['POST'])
def delete_node(node_id):
    # Update the data in the Firebase database to remove the node with the specified NodeID
    db.reference('Nodes').child(str(node_id)).delete()

    return redirect('/nodes')

#/* -------------------------------------------------------------------------- */#
#/*                                 Connection                                 */#
#/* -------------------------------------------------------------------------- */#

# Define a route to render the main page for connections
@app.route('/connections')
def connections_page():
    # Read data from Firebase (assuming you have a 'Connection' node)
    connection_data = db.reference('Connection').get()

    # Check if 'Connection' is not None
    if connection_data is not None:
        if isinstance(connection_data, list):
            # Convert the list to a dictionary with index as key
            connection_data = {str(index): connection for index, connection in enumerate(connection_data)}

        return render_template('connections.html', connection_data=connection_data)
    else:
        # If 'Connection' is None, pass an empty dictionary to the template
        return render_template('connections.html', connection_data={})
    
# Define a route to handle form submissions for connections
@app.route('/submit_connection', methods=['POST'])
def submit_connection_form():
    # Get the last connection index directly from Firebase
    connection_data = db.reference('Connection').get()
    
    if connection_data:
        # Convert the list to a dictionary with indices as keys
        connection_dict = {str(index + 1): connection for index, connection in enumerate(connection_data)}
        last_connection_index = max([int(key) for key in connection_dict.keys()], default=0)
    else:
        last_connection_index = 0

    # Create a new connection with an index one higher than the last one
    new_connection_name = str(last_connection_index)

    # Extract form data from the submitted form
    start = request.form.get('start')
    end = request.form.get('end')
    weight = request.form.get('weight')

    # Create a new connection in Firebase with the submitted data
    db.reference('Connection').child(new_connection_name).set({
        'start': start,
        'end': end,
        'weight': weight
    })

    # Redirect to the main page or any other appropriate URL
    return redirect('/connections')

# Define a route to handle connection updates
@app.route('/update_connection/<string:connection_id>', methods=['POST'])
def update_connection(connection_id):
    # Extract form data from the submitted form
    start = request.form.get('start')
    end = request.form.get('end')
    weight = request.form.get('weight')

    # Update the existing connection with the submitted data
    db.reference('Connection').child(connection_id).update({
        'start': start,
        'end': end,
        'weight': weight
    })

    # Redirect to the main page
    return redirect('/connections')

# Define a route to handle connection deletion
@app.route('/delete_connection/<int:connection_id>', methods=['POST'])
def delete_connection(connection_id):
    # Update the data in the Firebase database to remove the connection with the specified ConnectionID
    db.reference('Connection').child(str(connection_id)).delete()

    return redirect('/connections')

if __name__ == '__main__':
    app.run(debug=True)
