# Getting Started with Google Cloud Video Intelligence API

This project was guided by the Google Could Video AI.\
To run this project in the local development environment, follow the steps below.\
See https://cloud.google.com/video-intelligence for more information.

## Register/login into Google Could Platform
Follow the https://cloud.google.com to register or login gcloud platform.

## Set up Application Default Credentials
### What is Application Default Credentials (ADC) ?
ADC is a strategy used by the authentication libraries to automatically find 
credentials based on the application environment. The authentication libraries
make those credentials available to Could Client Libraries and Google API
Client Libraries. When you use ADC, your code can run in either a development
or production environment without changing how your application authenticate to
Google Could services and APIs. 
### Provide credentials to ADC under the local development environment.
#### User credentials
When your code is running in a local development environment, such as a development
workstation, the best option is to use the credentials associated with your 
Google Cloud Platform user account.\
When you configure ADC with your user account, you should be aware of the following
facts:
- ADC configuration with a user account might not work for some methods and APIs,
such as the Cloud Translation API or the Could Vision API. But for the Video
Intelligence API we used in this project, it's totally fine to continue with
user credentials.
- The local ADC file contains your refresh token. Any user with access to your
file system can use it to get a valid access token. If you no longer need these
local credentials, you can revoke them by using the `gcloud auth application-default
revoke` command.
- Your local ADC file is associated with your user account, not your gcloud CLI
configuration. Changing to a different gcloud CLI configuration might change the 
identity used by the gcloud CLI, but it does not affect your local ADC file or
the ADC configuration.
- By default, the access tokens generated from a local ADC file created with user
credentials include the cloud-wide scope https://www.googleapis.com/auth/cloud-platform.
To specify scopes explicitly, you use the `--scopes` flag with the `gcloud auth
application-default login` command.\
To add scopes for services outside of Google Could, such as Google Drive, create
- an OAuth Client ID and provide it to the `gcloud auth application-default login`
command by using the `--client-di-file` flag, specifying your scopes with the
`--scopes` flag. \

**Configure ADC with your Google Account:**
1. Install the gcloud CLI (For macOS):
- Confirm you have a supported version of Python: run `python3 -V` or `python -V`.
Supported versions are Python 3.8 to 3.12.
- Determine your machine hardware: run `uname -m`.
- Search for then specific package lines up with your machine hardware and download it.
2. Initialize the gcloud CLI: run `./google-cloud-sdk/bin/gcloud init`. Beware of
you are now under the directory where you download the google-cloud-sdk.
This command will take you through the configuration of gcloud. And it would 
ask you to log into your Google Could Platform account, pick up the project under
which you want to run this code and configure a default Compute Region and Zone.
If you do not configure these, it would set up to some default configuration.
3. Configure ADC: run `./goofle-cloud-adk/bin/gcloud auth application-default login`.
Then, a sign-in screen appears. After you sign in, your credentials are stored in
the local credential file used by ADC. On the terminal, you will see [Credentials
saved to file: [/home/User/.config/gcloud/application_default_credentials.json]]. 
These credentials will be used by any library that requests Application Default Credentials (ADC).
4. Authenticate to the video intelligence API: go https://cloud.google.com/video-intelligence/docs/common/auth
to enable the Video Intelligence API.

See https://cloud.google.com/docs/authentication/provide-credentials-adc for more information.

## Revise the Code Based on Your Need
The key package we've imported and used for detect subtitles from a video file is
**google.cloud.videointelligence.v1**. In this project, I've tried to use video 
annotation features **TEXT_DETECTION** and **SPEECH_TRANSCRIPTION** and found
that for low-quality videos with sound, **SPEECH_TRANSCRIPTION** tends to work
better but with lower speed due to different types of segmentation of the video.\
Go https://cloud.google.com/video-intelligence/docs/reference/rpc/google.cloud.videointelligence.v1#google.cloud.videointelligence.v1
for more information about this package. You can find detailed usage of 
interfaces, messages, and eums in the package. 