import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';

const workerId = "aWorker";
let currentId = "";

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

async function completeTask(taskId, width, height, depth, weight) {
  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  var raw = JSON.stringify({
    "workerId": workerId, "variables": {
      "packageDimensions": {
        "value": {
          'w': {"value": width}, 'h':  {"value": height}, 'd':  {"value": depth}
        }
      },
      "weight": { "value": weight }
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

function handleCompleteTask(event, width, height, depth, weight) {
  event.preventDefault();
  handleReserve().then(() => {
    fetchReserveTask().then(response => response.json())
      .then(result => {
        console.log(result);
        completeTask(currentId, width, height, depth, weight).then(e => console.log(e)).catch(e => console.error(e))
      })
      .catch(error => console.log('error', error));
  });
}

function App() {
  const [width, setWidth] = useState(0);
  const [height, setHeight] = useState(0);
  const [depth, setDepth] = useState(0);
  const [weight, setWeight] = useState(0);

  fetchExternalTasks()
    .then(e => e.json())
    .then(e => console.log(e))
    .catch(e => console.error(e));

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
          <div>Volumen: {width*height*depth} m³</div>
          <div>Gewicht: {weight} m³</div>
          <div><strong>Geschätzte Kosten: </strong> {Math.max(width*height*depth / 1000, weight)} € </div>
        </div>
        <form onSubmit={event => handleCompleteTask(event, width, height, depth, weight)}>

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
              <input type="text" value={weight} onChange={(event) => handleChange(setWeight, event)} placeholder="Weite" />
              <span> kg</span>
            </label>
          </div>
          <input type="submit" value="Abschicken" />
        </form>
      </div>
    </div>
  );
}

export default App;
