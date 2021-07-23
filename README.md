# lambda-layer-poc

This project shows how to implement an AWS lambda function using a [layer](https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html).

An AWS lambda layer can help to decouple some core functionalities between lambda functions avoiding to repeating code and common configurations.

### Project Architecture
This project is divided into two modules: api and repository.
- _api_ contains al business logic.
- _repository_ is the data access layer.

`repository` uses DynamoDb to access to the project data, but this module is build as an AWS Lambda Layer to reduce api artifact size.

Note:
This project can be optimized to the infinite (creating new layers, for example)
