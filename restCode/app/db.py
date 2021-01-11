import psycopg2 as psycopg2


def start_database():
    # TODO: Ping database until it is up...
    try:
        return psycopg2.connect(dbname='postgres', user='postgres', password='hackme', host='postgres',
                                options=f'-c search_path={"schema_package"}')
    except Exception as e:
        print("I am unable to connect to the database.", e)


class ShipmentDatabase:

    def __init__(self):
        self.conn = start_database()

    def getShipmentsMap(self, key):
        cur = self.conn.cursor()
        cur.execute("""SELECT c.customerId, si.shipmentid, si.barcode, 
                            ST_AsText(si.destination), ST_AsText(si.startlocation), si.attachedprocessinstance, si.price
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
                'price': float(record[6])
            })
        return el
