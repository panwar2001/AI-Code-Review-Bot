# AI-Code-Review-Bot
To enable Repository webhook in Github


## Repository Webhooks
### Follow these steps to send real-time notifications from a specific project to an external server:
1. Open your browser, navigate to your repository on GitHub, and click the Settings tab at the top of the page.
1. Look at the left navigation sidebar and click on Webhooks.
1. Click the Add webhook button in the top right corner.
1. Fill out the required configuration fields:
* * Payload URL: Provide the target server endpoint where GitHub will deliver HTTP POST requests.
* * Content type: Select application/json to receive the data format as a clean JSON object.
* * Secret: Type a secure, random string to validate that incoming payloads truly originate from GitHub.
* * SSL verification: Keep Enable SSL verification selected to protect data transfers.
5. Under Which events would you like to trigger this webhook?, choose to subscribe to standard push events, individual custom actions (like issues or pull requests), or all events.
6. Confirm the Active checkbox is flagged, then click Add webhook to complete the activation.

