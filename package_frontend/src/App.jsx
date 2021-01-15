import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';
import { locateMe, getGeoCoding, getReverseGeoCoding } from './utils/geoLocate'

const workerId = "aWorker";
let currentId = "";
//http://localhost:8080/engine-rest/engine/default/process-definition/key/PaketProzess/start

async function startProcess() {
  let myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const url = "http://localhost:8080/engine-rest/engine/default/process-definition/key/PaketProzess/start";
  const requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: '{}',
    redirect: 'follow'
  };

  return fetch(url, requestOptions);
}

async function fetchReserveTask(url, topicName, processInstanceId) {
  let myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");
  let topic = { "topicName": topicName, "lockDuration": 10000 };
  if (processInstanceId) topic.processInstanceId = processInstanceId;

  let raw = JSON.stringify({ "workerId": workerId, "maxTasks": 1, "topics": [topic] });

  let requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  return fetch(url, requestOptions);
}

async function handleReserve(url, topicName, processInstanceId) {
  let result = await fetchReserveTask(url, topicName, processInstanceId);
  result = await result.json();
  if (result.length > 0) {
    currentId = result[0].id;
    return currentId;
  }
  throw new Error("Unable to get id!");
}

async function completeTaskHelper(taskId, objectData) {
  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  var raw = JSON.stringify(objectData);

  var requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  return fetch("http://localhost:8080/engine-rest/external-task/" + taskId + "/complete", requestOptions);
}

async function completeTask(taskId, width, height, depth, weight, location, pTargetLocation, pAPIKey) {
  console.log(taskId)
  var raw = {
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
  };

  return completeTaskHelper(taskId, raw);
}

async function getMyShipments(apiKey) {
  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");
  var raw = JSON.stringify({
    "apiKey": apiKey
  });
  var requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  return fetch("http://localhost:8082/shipments/all", requestOptions);
}


async function completeConfirmShipment(taskId, shipmentId, pAPIKey) {
  let raw = {
    "workerId": workerId, "variables": {
      "shipmentId": { "value": shipmentId },
      "key": { "value": pAPIKey }
    }
  };

  return completeTaskHelper(taskId, raw);
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

    startProcess()
      .then(() => handleReserve("http://localhost:8080/engine-rest/external-task/fetchAndLock", "createPackage"))
      .then(taskId => completeTask(taskId, pWidth, pHeight, pDepth, pWeight, pLocation, pTargetLocation, pAPIKey))
      .catch(error => console.log('error', error));
  }

  function confirmShipment(shipmentId, processInstanceId) {
    let pAPIKey = apiKey.valueOf();

    handleReserve("http://localhost:8080/engine-rest/external-task/fetchAndLock", "collectionRequest", processInstanceId).then(e => completeConfirmShipment(currentId, shipmentId, pAPIKey));
  }


  /*fetchExternalTasks()
    .then(e => e.json())
    .then(e => console.log(e))
    .catch(e => console.error(e));*/

  function handleChange(fn, event) {
    let val = (event.target.value);
    fn(val ? val : 0);
  }

  function handleChangeInt(fn, event) {
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
              <input type="number" value={width} onChange={(event) => handleChangeInt(setWidth, event)} placeholder="Weite" /> <span> x </span>
              <input type="number" value={height} onChange={(event) => handleChangeInt(setHeight, event)} placeholder="Höhe" /> <span> x </span>
              <input type="number" value={depth} onChange={(event) => handleChangeInt(setDepth, event)} placeholder="Tiefe" />
              <span> Meter</span>
            </label>
          </div>

          <div>
            <label>
              Gewicht:
              <input type="text" value={weight} onChange={(event) => handleChangeInt(setWeight, event)} placeholder="Gewicht" />
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
              <tr>
                <th>Customer Id</th>
                <th>Shipment Id</th>
                <th>Preis</th>
                <th>Destination</th>
                <th>Start Location</th>
                <th>Attached Instance Id</th>
                <th>Barcode</th>
                <th>Zustand</th>
                <th>Action</th>
              </tr>
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
                    <td>{e.state}</td>
                    <td>
                      {e.barcode ?
                        e.state === 'INFO_RECEIVED' ?
                        (<div>
                          <button>Cancel</button>
                          <button onClick={() => confirmShipment(e.shipmentId, e.attachedProcessInstance)}>Confirm</button>
                        </div>) : "Verschickt" : "Waiting for manual confirmation"
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
