#!/bin/sh

openssl aes-256-cbc -K $KYS_CREDENTIALS_KEY -iv $KYS_CREDENTIALS_IV -in resources/credentials.tar.enc -out resources/credentials.tar -d
tar xvf resources/credentials.tar
