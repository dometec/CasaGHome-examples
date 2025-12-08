# CasaGHome Example Application

This project provides a sample application built with the [CasaGHome](https://github.com/dometec/CasaGHome) library, an helper library designed to make Google Home integrations much easier!
In this demo, we simulate a light that changes its state every minute. Its state can also be controlled from the Google Home app, a Google Speaker (Nest Mini) or Google Smart Display (Nest Hub).

> Note: This application simulates an OAuth server with *FAKE autentication*. It is intended only as a demonstration*.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

# How run this Exmaple

### Expose the Application Publicly

The Google Home platform requires a publicly reachable endpoint. To achieve this quickly, we can use [ngrok](https://ngrok.com/) (or [localtunnel](https://localtunnel.app/), [tunnelmole](https://tunnelmole.com/) or tool like that).

Install and start ngrok with:
```bash
ngrok http http://localhost:8080
```
You will see an output similar to this:
```bash
Session Status      online
Account             dometec@gmail.com (Plan: Free)
Update              update available (version 3.33.1, Ctrl-U to update)
Version             3.33.0
Region              Europe (eu)
Web Interface       http://127.0.0.1:4040
Forwarding          https://04a92a6c3e41.ngrok-free.app -> http://localhost:8080 
```
Take note of the *Forwarding HTTPS address* — you’ll need it in the next steps.

### Configure the Google Home Project

Now we have to configure Google Home to use our service. You need a create a new project in [Google Home Console](https://console.home.google.com/projects). 

Next, create a new Cloud-To-Cloud integration and set its Name, Device Type (just a Light is OK) and a Branding image.

In Account Linking, put *fake* in both OAuth Client ID and Client Secret.<br>
In Authorization URL set the previous address with "/fakeauth/authorize" (es: https://7c37cc8f951d.ngrok-free.app/fakeauth/authorize).<br>
In Token URL set the previous address with "/fakeauth/token" (es: https://7c37cc8f951d.ngrok-free.app/fakeauth/token).<br>
For Cloud fulfillment URL use "/smarthome" (es: https://7c37cc8f951d.ngrok-free.app/smarthome).<br>

Screenshots of the configuration steps can be found here: [1](Schermata_20251201_153208.png) [2](Schermata_20251201_153229.png) [3](Schermata_20251201_153240.png)

### HomeGraph Service Account

You will also need a service account to call Google HomeGraph API, so follow the [guide](https://developers.home.google.com/cloud-to-cloud/integration/report-state?authuser=0#get_started) and enable the HomeGraph API in you Google Cloud Console and create the service account to access it. On the service account page, you have to create the Key, as JSON File, and download it. Put the file in src/main/resources/ and use the file name in _SA_FILE_ environment variable like showed below.

### Start the Application

Start the project as usual with (*replace serviceaccount_credential_file.json with the name of the file that you just download in src/main/resources/):
```bash
mvn quarkus:dev -DSA_FILE=serviceaccount_credential_file.json

```
It'll now be listening on localhost:8080 and waiting for requests from Google.

### Link Your Integration in Google Home

At this point you should have a working integration. Open the Google Home App, navigate to Settings -> Working with Google -> Integrate with other devices. You will find your integration listed with a [test] prefix.

Select it, complete the (fake) login process, and your virtual lamp should appear — ready to be controlled.

