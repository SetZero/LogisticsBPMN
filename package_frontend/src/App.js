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

function handleReserve() {
  fetchReserveTask().then(response => response.json())
    .then(result => {
      if(result.length > 0) {
        currentId = result[0].id;
        console.log(result[0]);
      }
    })
    .catch(error => console.log('error', error));
}

async function completeTask(taskId) {
  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  var raw = JSON.stringify({
    "workerId": workerId, "variables": {
      "packageDimensions": {
        "value": {
          'w': 1, 'h': 2, 'd': 3
        }
      },
      "weight": { "value": 42 }
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

function handleCompleteTask() {
  fetchReserveTask().then(response => response.text())
    .then(result => {
      console.log(result);
      completeTask(currentId);
    })
    .catch(error => console.log('error', error));
}

function App() {
  fetchExternalTasks()
    .then(e => e.json())
    .then(e => console.log(e))
    .catch(e => console.error(e));

  return (
    <div className="App">
      <header className="App-header">
        Paketverwaltung
      </header>
      <div>
        <button onClick={handleReserve}>Reservieren</button>
        <button onClick={handleCompleteTask}>Abschicken</button>
      </div>
    </div>
  );
}

export default App;
