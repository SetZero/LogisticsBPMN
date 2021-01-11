import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';

const workerId = "aWorker";
let currentId = "";
//http://localhost:8080/engine-rest/engine/default/process-definition/key/PaketProzess/start


async function fetchExternalTasks() {
  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  var raw = JSON.stringify({ "topicName": "createPackage" });

  var requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  return fetch("http://localhost:8080/engine-rest/external-task/", requestOptions);
}

async function getGeoCoding(country, postalcode, street) {
  let url = "https://nominatim.openstreetmap.org/search?country=" + country + "&postalcode=" + postalcode + "&street=" + street + "&format=json";
  return fetch(url);
}

async function getReverseGeoCoding(lat, long) {
  let url = "https://nominatim.openstreetmap.org/reverse?lat="+lat+"&lon="+long+"&format=json";
  return fetch(url);
}

async function fetchReserveTask() {
  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  var raw = JSON.stringify({ "workerId": workerId, "maxTasks": 1, "topics": [{ "topicName": "createPackage", "lockDuration": 10000 }] });

  var requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  return fetch("http://localhost:8080/engine-rest/external-task/fetchAndLock", requestOptions);
}

async function handleReserve() {
  let result = await fetchReserveTask().then(response => response.json());
  if (result.length > 0) {
    currentId = result[0].id;
    console.log(currentId);
  }
}

async function completeTask(taskId, width, height, depth, weight, location, pTargetLocation, pAPIKey) {
  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  var raw = JSON.stringify({
    "workerId": workerId, "variables": {
      "packageDimensions": {
        "value": {
          'w': width, 'h': height, 'd': depth
        }
      },
      "weight": { "value": weight },
      "location": { "value": location },
      "targetLocation": { "value": pTargetLocation },
      "key": { "value": pAPIKey }
    }
  });

  var requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  return fetch("http://localhost:8080/engine-rest/external-task/" + taskId + "/complete", requestOptions);
}

async function getMyShipments(apiKey) {
  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");
  var raw = JSON.stringify({
    "apiKey": "HACKME"
  });
  var requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  return fetch("http://localhost:8082/shipments/all", requestOptions);
}

function locateMe(writeTo) {

  navigator.geolocation.getCurrentPosition((loc) => {
    let long = loc.coords.longitude;
    let lat = loc.coords.latitude;
    writeTo(long + " " + lat);
    getReverseGeoCoding(lat, long).then(e => e.json()).then(e => {
      console.log(e);
    });
  });
}

function App() {
  const [width, setWidth] = useState(0);
  const [height, setHeight] = useState(0);
  const [depth, setDepth] = useState(0);
  const [weight, setWeight] = useState(0);
  const [location, setLocation] = useState("");
  const [targetLocation, setTargetLocation] = useState("11.070280570708459 49.41817197216562");
  const [apiKey, setApiKey] = useState("HACKME");

  const [myShipments, setMyShipments] = useState([]);

  function handleCompleteTask(event) {
    event.preventDefault();
    let pWidth = width.valueOf();
    let pHeight = height.valueOf();
    let pDepth = depth.valueOf();
    let pWeight = weight.valueOf();
    let pLocation = location.valueOf();
    let pTargetLocation = targetLocation.valueOf();
    let pAPIKey = apiKey.valueOf();
    handleReserve().then(() => {
      fetchReserveTask().then(response => response.json())
        .then(result => {
          console.log(result);
          completeTask(currentId, pWidth, pHeight, pDepth, pWeight, pLocation, pTargetLocation, pAPIKey).then(e => console.log(e)).catch(e => console.error(e))
        })
        .catch(error => console.log('error', error));
    });
  }

  /*fetchExternalTasks()
    .then(e => e.json())
    .then(e => console.log(e))
    .catch(e => console.error(e));*/

  function handleChange(fn, event) {
    let val = parseInt(event.target.value);
    fn(val ? val : 0);
  }

  return (
    <div className="App">
      <header className="App-header">
        Paketverwaltung
      </header>
      <div>
        <div>
          <h2>Zusammenfassung</h2>
          <div>Volumen: {width * height * depth} m³</div>
          <div>Gewicht: {weight} m³</div>
          <div><strong>Geschätzte Kosten: </strong> {Math.max(width * height * depth / 1000, weight)} € </div>
        </div>
        <form onSubmit={event => handleCompleteTask(event)}>

          <div>
            <label>
              Größe:
              <input type="number" value={width} onChange={(event) => handleChange(setWidth, event)} placeholder="Weite" /> <span> x </span>
              <input type="number" value={height} onChange={(event) => handleChange(setHeight, event)} placeholder="Höhe" /> <span> x </span>
              <input type="number" value={depth} onChange={(event) => handleChange(setDepth, event)} placeholder="Tiefe" />
              <span> Meter</span>
            </label>
          </div>

          <div>
            <label>
              Gewicht:
              <input type="text" value={weight} onChange={(event) => handleChange(setWeight, event)} placeholder="Gewicht" />
              <span> kg</span>
            </label>
          </div>

          <div>
            <label>
              Abholungsort:
              <input type="text" value={location} onChange={(event) => handleChange(setLocation, event)} placeholder="Ort" readOnly />
              <button type="button" onClick={() => locateMe(setLocation)}>Lokalisieren</button>
            </label>
          </div>

          <div>
            <label>
              Lieferort:
              <input type="text" value={targetLocation} onChange={(event) => handleChange(setTargetLocation, event)} placeholder="Ort" readOnly />
            </label>
          </div>

          <div>
            <label>
              API Key:
              <input type="password" value={apiKey} onChange={(event) => handleChange(setApiKey, event)} placeholder="Ort" />
            </label>
          </div>
          <input type="submit" value="Abschicken" />
        </form>
        <div>
          <button onClick={() => getMyShipments(apiKey.valueOf())
            .then((e) => {
              e.json().then((j) => setMyShipments(j))
            })
          }>Get Shipments!</button>

          <table>
            <thead>
              <th>Customer Id</th>
              <th>Shipment Id</th>
              <th>Preis</th>
              <th>Destination</th>
              <th>Start Location</th>
              <th>Attached Instance Id</th>
              <th>Barcode</th>
              <th>Action</th>
            </thead>
            <tbody>
              {
                myShipments.map((e, i) => {
                  return (<tr key={i}>
                    <td>{e.customerId}</td>
                    <td>{e.shipmentId}</td>
                    <td>{e.price ?? '---'} €</td>
                    <td>{e.destination}</td>
                    <td>{e.startLocation}</td>
                    <td>{e.attachedProcessInstance}</td>
                    <td>{e.barcode ? (<img src={"data:image/png;base64," + e.barcode} alt="barcode" />) : "---"}</td>
                    <td>
                      {e.barcode ?
                        (<div>
                          <button>Cancel</button>
                          <button>Confirm</button>
                        </div>) : "Waiting for manual confirmation"
                      }
                    </td>
                  </tr>)
                })
              }
            </tbody>
          </table>
        </div>
      </div>
    </div >
  );
}

export default App;
