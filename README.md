# fhir-java-lambda

This is an implementation of Lambda function for converting FHIR sample for Java in Cloud9.

![Workflow](/images/workflow.png)


Cloud9 is an environment for coding in the AWS Cloud. [Cloud9](https://aws.amazon.com/jp/cloud9/)

         ___        ______     ____ _                 _  ___  
        / \ \      / / ___|   / ___| | ___  _   _  __| |/ _ \ 
       / _ \ \ /\ / /\___ \  | |   | |/ _ \| | | |/ _` | (_) |
      / ___ \ V  V /  ___) | | |___| | (_) | |_| | (_| |\__, |
     /_/   \_\_/\_/  |____/   \____|_|\___/ \__,_|\__,_|  /_/ 
 ----------------------------------------------------------------- 
 

![Architecture](/images/architecture-1.png)

The project source includes function code and supporting resources:

- `src/main` - A Java function.
- `src/test` - A unit test and helper classes.
- `template.yml` - An AWS CloudFormation template that creates an application.
- `build.gradle` - A Gradle build file.
- `1-create-bucket.sh`, `2-build-layer.sh`, etc. - Shell scripts that use the AWS CLI to deploy and manage the application.
- `test` - Some samples for testing.

Use the following instructions to deploy the sample application.

# Requirements
- [Java 11 runtime environment (SE JRE)](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Gradle 5](https://gradle.org/releases/)
- The Bash shell. For Cloud9, Linux and macOS, this is included by default.
- [HAPI HL7v2](https://hapifhir.github.io/hapi-hl7v2/getting_started.html)
- [The AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html) v1.17 or newer.

If you use the AWS CLI v2, add the following to your [configuration file](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html) (`~/.aws/config`):


```
cli_binary_format=raw-in-base64-out
```

This setting enables the AWS CLI v2 to load JSON events from a file, matching the v1 behavior.

# Setup
Download or clone this repository and setup environment.

    $ git clone https://github.com/MasazI/fhir-java-lambda.git
    $ ./0-setup-cloud9.sh
    $ export PATH=$PATH:/opt/gradle/gradle-6.7/bin
    $ export AWS_DEFAULT_REGION=<your region>

To create a new bucket for deployment artifacts, run `1-create-bucket.sh`.

    fhir-java-lambda$ ./1-create-bucket.sh
    (example )make_bucket: lambda-artifacts-a5e491dbb5b22e0d

To build a Lambda layer that contains the function's runtime dependencies, run `2-build-layer.sh`. Packaging dependencies in a layer reduces the size of the deployment package that you upload when you modify your code.

    fhir-java-lambda$ ./2-build-layer.sh

# Deploy

Set environment variable below,\

    export ENV_USERNAME='<username>'
    export ENV_PASSWORD='<password>'
    export ENV_ACCESS_KEY='<aws access key>'
    export ENV_SECRET_KEY='<aws secret key>'
    export ENV_USER_POOL='<cognito user pool id>'
    export ENV_CLIENT_ID='<cognito client id>'
    export ENV_API_KEY='<api gateway key>'
    export ENV_API_END_POINT='<api gateway endpoint host and stage path>'


To deploy the application, run `3-deploy.sh`.

    fhir-java-lambda$ ./3-deploy.sh
    BUILD SUCCESSFUL in 1s
    Successfully packaged artifacts and wrote output template to file out.yml.
    Waiting for changeset to be created..
    Successfully created/updated stack - fhir-java-lambda

This script uses AWS CloudFormation to deploy the Lambda functions and an IAM role. If the AWS CloudFormation stack that contains the resources already exists, the script updates it with any changes to the template or function code.

After deployment, set the same environment variables into Lambda.

    ENV_USERNAME='<username>'
    ENV_PASSWORD='<password>'
    ENV_ACCESS_KEY='<aws access key>'
    ENV_SECRET_KEY='<aws secret key>'
    ENV_USER_POOL='<cognito user pool id>'
    ENV_CLIENT_ID='<cognito client id>'
    ENV_API_KEY='<api gateway key>'
    ENV_API_END_POINT='<api gateway endpoint host and stage path>'
    
# Test
To invoke the function, run `4-upload.sh`.

    fhir-java-lambda$ ./4-upload.sh
    upload: test/test.txt to s3://<the application bucket>/inbound/test.txt

Let the script invoke the function a few times and then press `CRTL+C` to exit.

The application uses AWS X-Ray to trace requests. Open the [X-Ray console](https://console.aws.amazon.com/xray/home#/service-map) to view the service map.

![Service Map](/images/service-map.png)

Choose a node in the main function graph. Then choose **View traces** to see a list of traces. Choose any trace to view a timeline that breaks down the work done by the function.

![Trace](/images/trace.png)

Additionally, you can trace each segment.

![Trace Segment](/images/trace-segment.png)


Finally, view the application in the Lambda console.

*To view the application*
1. Open the [applications page](https://console.aws.amazon.com/lambda/home#/applications) in the Lambda console.
2. Choose **fhir-java-lambda**.

# Cleanup
To delete the application, run `5-cleanup.sh`.

    blank$ ./5-cleanup.sh
