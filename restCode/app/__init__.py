from flask import Flask
from flask_cors import CORS
from flask import request
import json
import hashlib
from app import db

app = Flask(__name__)
cors = CORS(app)

conn_db = db.ShipmentDatabase()


@app.route('/')
def hello_world():
    return json.dumps({'error': 'INVALID ACCESS'})


@app.route('/bla')
def bla():
    el = conn_db.getShipmentsMap("ABC")
    return json.dumps({'error': 'INVALID ACCESS'})


@app.route('/shipments/all', methods=['POST'])
def get_all_shipments():
    key = hashlib.sha3_256(json.loads(request.data)["apiKey"].encode('utf-8')).hexdigest()
    print(key)

    el = conn_db.getShipmentsMap(key)
    return json.dumps(el)


@app.route('/shipments/by-process/<process_id>', methods=['GET'])
def get_shipments_by_process(process_id):
    print("process_id: %s" % process_id)
    return json.loads(request.data)["apiKey"]
