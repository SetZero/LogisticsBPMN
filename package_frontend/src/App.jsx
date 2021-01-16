import React, { useState } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import Button from '@material-ui/core/Button';
import ButtonGroup from '@material-ui/core/ButtonGroup';
import DeleteIcon from '@material-ui/icons/Delete';
import GpsFixedIcon from '@material-ui/icons/GpsFixed';
import UpdateIcon from '@material-ui/icons/Update';
import TablePagination from '@material-ui/core/TablePagination';
import Typography from '@material-ui/core/Typography';
import Card from '@material-ui/core/Card';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import TableRow from '@material-ui/core/TableRow';
import useScrollTrigger from '@material-ui/core/useScrollTrigger';
import AppBar from '@material-ui/core/AppBar';
import Slide from '@material-ui/core/Slide';
import Toolbar from '@material-ui/core/Toolbar';
import Box from '@material-ui/core/Box';
import Container from '@material-ui/core/Container';
import TextField from '@material-ui/core/TextField';
import InputAdornment from '@material-ui/core/InputAdornment';
import LabelImportantIcon from '@material-ui/icons/LabelImportant';
import WidgetsIcon from '@material-ui/icons/Widgets';
import EmojiPeopleIcon from '@material-ui/icons/EmojiPeople';
import CompareArrowsIcon from '@material-ui/icons/CompareArrows';
import ScannerIcon from '@material-ui/icons/Scanner';
import LocalShippingIcon from '@material-ui/icons/LocalShipping';
import HowToRegIcon from '@material-ui/icons/HowToReg';
import ErrorIcon from '@material-ui/icons/Error';
import CancelIcon from '@material-ui/icons/Cancel';
import RemoveCircleIcon from '@material-ui/icons/RemoveCircle';
import CircularProgress from '@material-ui/core/CircularProgress';
import Grid from '@material-ui/core/Grid';
import Tooltip from '@material-ui/core/Tooltip';
import './App.css';
import { locateMe, getGeoCoding, getReverseGeoCoding } from './utils/geoLocate';

const workerId = "aWorker";
let currentId = "";

function HideOnScroll(props) {
  const { children, window } = props;
  // Note that you normally won't need to set the window ref as useScrollTrigger
  // will default to window.
  // This is only being set here because the demo is in an iframe.
  const trigger = useScrollTrigger({ target: window ? window() : undefined });

  return (
    <Slide appear={false} direction="down" in={!trigger}>
      {children}
    </Slide>
  );
}

function getIconForPackageState(state) {
  switch (state) {
    case 'INFO_RECEIVED':
      return (<Tooltip title="Elektronische Ankündigung erhalten"><LabelImportantIcon /></Tooltip>);
    case 'COLLECTION_REQUEST':
      return (<Tooltip title="Abholungsanfrage erhalten"><WidgetsIcon /></Tooltip>);
    case 'PICK_UP':
      return (<Tooltip title="Paket vom Ursprungsort aufgesammelt"><EmojiPeopleIcon /></Tooltip>);
    case 'IN_TRANSIT':
      return (<Tooltip title="Paket in Transit"><CompareArrowsIcon /></Tooltip>);
    case 'HUB_SCAN':
      return (<Tooltip title="Paket in Verteilerzentrum aufgesammelt"><ScannerIcon /></Tooltip>);
    case 'OUT_FOR_DELIVERY':
      return (<Tooltip title="In Zustellung"><LocalShippingIcon /></Tooltip>);
    case 'DELIVERED':
      return (<Tooltip title="Zugestellt"><HowToRegIcon /></Tooltip>);
    case 'DECLINED':
      return (<Tooltip title="Versand abgelehnt - Support kontaktieren"><CancelIcon /></Tooltip>);
    default:
      return (<Tooltip title="Unbekannt"><ErrorIcon /></Tooltip>);
  }
}

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

async function completeCancelShipment(taskId, shipmentId, pAPIKey) {
  let raw = {
    "workerId": workerId, "variables": {
      "shipmentId": { "value": shipmentId },
      "key": { "value": pAPIKey },
      "cancelShipment": { "value": "true" }
    }
  };

  return completeTaskHelper(taskId, raw);
}

const useStyles = makeStyles({
  root: {
    width: '100%',
  },
  card_root: {
    minWidth: 275,
  },
  container: {
    maxHeight: 840,
  },
  title: {
    fontSize: 16,
  },
  pos: {
    marginBottom: 12,
    fontSize: 12,
  },
  barcode: {
    maxWidth: 320
  }
});

function App() {
  const [width, setWidth] = useState(0);
  const [height, setHeight] = useState(0);
  const [depth, setDepth] = useState(0);
  const [weight, setWeight] = useState(0);
  const [location, setLocation] = useState("");
  const [targetLocation, setTargetLocation] = useState("11.070280570708459 49.41817197216562");
  const [apiKey, setApiKey] = useState("HACKME");
  const [packageLoading, setPackageLoading] = useState(false);
  const classes = useStyles();

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

    setPackageLoading(true);
    startProcess()
      .then(() => handleReserve("http://localhost:8080/engine-rest/external-task/fetchAndLock", "createPackage"))
      .then(taskId => completeTask(taskId, pWidth, pHeight, pDepth, pWeight, pLocation, pTargetLocation, pAPIKey))
      .then(() => {
        setPackageLoading(false);
        updateShipmentData();
      })
      .catch(error => console.log('error', error));
  }

  async function confirmShipment(shipmentId, processInstanceId) {
    let pAPIKey = apiKey.valueOf();

    return handleReserve("http://localhost:8080/engine-rest/external-task/fetchAndLock", "collectionRequest", processInstanceId).then(e => completeConfirmShipment(currentId, shipmentId, pAPIKey));
  }

  async function cancelShipment(shipmentId, processInstanceId) {
    let pAPIKey = apiKey.valueOf();

    return handleReserve("http://localhost:8080/engine-rest/external-task/fetchAndLock", "collectionRequest", processInstanceId).then(e => completeCancelShipment(currentId, shipmentId, pAPIKey));
  }

  function updateShipmentData() {
    getMyShipments(apiKey.valueOf())
      .then((e) => {
        e.json().then((j) => setMyShipments(j))
      })
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
      <HideOnScroll>
        <AppBar>
          <Toolbar>
            <Typography variant="h6">Paketverwaltung</Typography>
          </Toolbar>
        </AppBar>
      </HideOnScroll>
      <Box m={10} />
      <Box>
        <Box mt={5}>
          <Container>
            <Card className={classes.card_root} variant="outlined">
              <CardContent>
                <Typography className={classes.title} color="textSecondary" gutterBottom>
                  Zusammenfassung
            </Typography>
                <Typography variant="h5" component="h2">
                  {Math.max(width * height * depth / 1000, weight)} € <sup>*</sup>
                </Typography>
                <Typography className={classes.pos} color="textSecondary">
                  *Geschätzte Kosten
            </Typography>
                <Typography variant="body2" component="p">
                  <div>Volumen: {width * height * depth} cm³</div>
                  <div>Gewicht: {weight} kg</div>
                </Typography>
              </CardContent>
            </Card>
          </Container>
        </Box>

        <Box mt={5}>
          <Container>
            <Paper className={classes.root}>
              <form onSubmit={event => handleCompleteTask(event)}>
                <Container component="main">
                  <Grid container spacing={2}>
                    <Grid item xs={6} sm={4}>
                      <TextField type="number"
                        value={width}
                        onChange={(event) => handleChangeInt(setWidth, event)}
                        label="Weite"
                        variant="outlined"
                        fullWidth
                        InputProps={{
                          endAdornment: <InputAdornment position="end">cm</InputAdornment>,
                        }}
                        required
                      />
                    </Grid><Grid item xs={6} sm={4}>
                      <TextField type="number"
                        value={height}
                        onChange={(event) => handleChangeInt(setHeight, event)}
                        label="Höhe"
                        variant="outlined"
                        fullWidth
                        InputProps={{
                          endAdornment: <InputAdornment position="end">cm</InputAdornment>,
                        }}
                        required
                      />
                    </Grid><Grid item xs={12} sm={4}>
                      <TextField type="number"
                        value={depth}
                        onChange={(event) => handleChangeInt(setDepth, event)}
                        label="Tiefe"
                        variant="outlined"
                        fullWidth
                        InputProps={{
                          endAdornment: <InputAdornment position="end">cm</InputAdornment>,
                        }}
                        required
                      />
                    </Grid>
                  </Grid>
                  <Box m={2} />
                  <Grid item xs={12} sm={12} mt={10}>
                    <TextField
                      label="Gewicht"
                      value={weight} onChange={(event) => handleChangeInt(setWeight, event)}
                      InputProps={{
                        endAdornment: <InputAdornment position="end">Kg</InputAdornment>,
                      }}
                      variant="outlined"
                      fullWidth
                      required
                    />
                  </Grid>
                  <Box m={2} />
                  <Grid item xs={12} sm={12} mt={10}>
                    <TextField
                      label="Abholungsort"
                      value={location} onChange={(event) => handleChange(setLocation, event)}
                      InputProps={{
                        endAdornment: <Button type="button" onClick={() => locateMe(setLocation)}><GpsFixedIcon /></Button>,
                      }}
                      variant="outlined"
                      fullWidth
                      required
                    />
                  </Grid>
                  <Box m={2} />
                  <Grid item xs={12} sm={12} mt={10}>
                    <TextField
                      label="Lieferort"
                      value={targetLocation} onChange={(event) => handleChange(setTargetLocation, event)}
                      variant="outlined"
                      fullWidth
                      required
                    />
                  </Grid>
                  <Box m={2} />
                  <Grid item xs={12} sm={12} mt={10}>
                    <TextField
                      label="API Key"
                      value={apiKey} onChange={(event) => handleChange(setApiKey, event)}
                      variant="outlined"
                      type="password"
                      fullWidth
                      required
                    />
                  </Grid>
                  <Box m={2} />
                  <Button variant="contained" color="primary" type="submit" disabled={packageLoading}>
                    {packageLoading ? (<CircularProgress />) : "Sendung erstellen"}
                  </Button>
                  <Box m={2} />
                </Container>
              </form>
            </Paper>
          </Container>
        </Box>

        <Box mt={5}>
          <Container>
            <Button onClick={() => updateShipmentData()}><UpdateIcon /> Sendungen Aktialisieren</Button>

            <Paper className={classes.root}>
              <TableContainer className={classes.container}>
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      <TableCell>Kunden Nr.</TableCell>
                      <TableCell>Sendungs Nr.</TableCell>
                      <TableCell>Preis</TableCell>
                      <TableCell>Zielort</TableCell>
                      <TableCell>Startort</TableCell>
                      <TableCell>Barcode</TableCell>
                      <TableCell>Zustand</TableCell>
                      <TableCell>Aktion</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {
                      myShipments.map((e, i) => {
                        return (<TableRow hover role="checkbox" tabIndex={-1} key={i}>
                          <TableCell>{e.customerId}</TableCell>
                          <TableCell>{e.shipmentId}</TableCell>
                          <TableCell>{e.price ?? '---'} €</TableCell>
                          <TableCell>{e.destination}</TableCell>
                          <TableCell>{e.startLocation}</TableCell>
                          <TableCell>{e.barcode ? (<img className={classes.barcode} src={"data:image/png;base64," + e.barcode} alt="barcode" />) : "---"}</TableCell>
                          <TableCell>{getIconForPackageState(e.state)}</TableCell>
                          <TableCell>
                            {e.barcode ?
                              e.state === 'INFO_RECEIVED' ?
                                (<ButtonGroup size="small" aria-label="small outlined button group">
                                  <Button variant="contained" color="primary" onClick={() => confirmShipment(e.shipmentId, e.attachedProcessInstance).then(() => updateShipmentData())}>Bestätigen</Button>
                                  <Button variant="outlined" color="secondary" onClick={() => cancelShipment(e.shipmentId, e.attachedProcessInstance).then(() => updateShipmentData())}><DeleteIcon /></Button>
                                </ButtonGroup>) : (<Button disabled variant="contained" fullWidth><RemoveCircleIcon /></Button>) : (e.state === 'DECLINED' ? "Bestllung abgelehnt" : "Wird verarbeitet")
                            }
                          </TableCell>
                        </TableRow>)
                      })
                    }
                  </TableBody>
                </Table>
              </TableContainer>
            </Paper>
          </Container>
        </Box>
      </Box>
    </div >
  );
}

export default App;
