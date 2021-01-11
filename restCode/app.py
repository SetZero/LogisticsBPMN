import psycopg2 as psycopg2
from flask import Flask
from flask import request
import json
import hashlib

app = Flask(__name__)


def start_database():
    try:
        return psycopg2.connect(dbname='postgres', user='postgres', password='hackme', host='localhost',
                                options=f'-c search_path={"schema_package"}')
    except Exception  as e:
        print("I am unable to connect to the database.", e)


conn = start_database()


@app.route('/')
def hello_world():
    return json.dumps({'error': 'INVALID ACCESS'})


@app.route('/shipments/all', methods=['GET'])
def get_all_shipments():
    key = hashlib.sha3_256(json.loads(request.data)["apiKey"].encode('utf-8')).hexdigest()
    print(key)

    cur = conn.cursor()
    cur.execute("""SELECT c.customerId, si.shipmentid, si.barcode, 
                        ST_AsText(si.destination), ST_AsText(si.startlocation), si.attachedprocessinstance
                    FROM customer c
                    INNER JOIN shipmentinfo si ON si.customerid = c.customerid
                    WHERE c.customerApiKey ILIKE %s""",
                (key,))

    el = []
    for record in cur:
        el.append({
            'customerId': record[0],
            'shipmentId': record[1],
            'barcode': record[2],
            'destination': record[3],
            'startLocation': record[4],
            'attachedProcessInstance': record[5]
        })
    return json.dumps(el)


@app.route('/shipments/by-process/<process_id>', methods=['GET'])
def get_shipments_by_process(process_id):
    print("process_id: %s" % process_id)
    return json.loads(request.data)["apiKey"]


if __name__ == '__main__':
    app.run()
