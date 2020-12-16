import logo from './logo.svg';
import './App.css';


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

function App() {
  fetchExternalTasks()
  .then(e => e.json)
  .then(e => console.log(e))
  .catch(e => console.error(e));

  return (
    <div className="App">
      <header className="App-header">
        Paketverwaltung
      </header>
      <div>

      </div>
    </div>
  );
}

export default App;
