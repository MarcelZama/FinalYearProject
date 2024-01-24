### TO RUN -->>> python3 app.py

from flask import Flask, render_template, request, redirect
import firebase_admin
from firebase_admin import credentials, db

app = Flask(__name__)

# Initialize Firebase with your credentials
cred = credentials.Certificate("/Users/marcis578/Documents/University/Project/PythonWebsite/yearproject-4a736-firebase-adminsdk-hdwrj-69bb4f7dae.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://yearproject-4a736-default-rtdb.firebaseio.com/'
})

# Define a route to render the main page
@app.route('/')
def index():
    # Read data from Firebase (assuming you have a 'message' node)
    message = db.reference('message').get()

    # Check if 'message' is not None
    if message is not None:
        return render_template('index.html', message=message)
    else:
        # If 'message' is None, pass an empty string to the template
        return render_template('index.html', message='')

# Define a route to handle form submission and write to Firebase
@app.route('/submit', methods=['POST'])
def submit():
    message_text = request.form.get('message')
    # Write data to Firebase under 'message' node
    db.reference('message').set(message_text)
    return redirect('/')

if __name__ == '__main__':
    app.run(debug=True)
