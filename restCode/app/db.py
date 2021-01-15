import sys
import time

import psycopg2 as psycopg2
import os


def start_database():
    try:
        while True:
            try:
                conn = psycopg2.connect(dbname=os.environ['DB_NAME'], user=os.environ['DB_USERNAME'], password=os.environ['DB_PASSWORD'], host=os.environ['DB_HOSTNAME'],
                                    options=f'-c search_path={"schema_package"}')
                cur = conn.cursor()
                cur.execute('''select * from customer''')
            except psycopg2.OperationalError as e:
                print("Database is down: ", e)
                time.sleep(0.5)
                continue
            return conn
    except KeyError as e:
        raise Exception("Environment variable is missing: ", e)


class ShipmentDatabase:

    def __init__(self):
        self.conn = start_database()

    def getShipmentsMap(self, key):
        cur = self.conn.cursor()
        cur.execute("""SELECT c.customerId, si.shipmentid, si.barcode, 
                            ST_AsText(si.destination), ST_AsText(si.startlocation), si.attachedprocessinstance,
                            si.price, si.state
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
                'attachedProcessInstance': record[5],
                'price': float(record[6]),
                'state': record[7],
            })
        return el
