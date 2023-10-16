#!/bin/bash

rm -rf okta/*.class
javac -cp okta-jars/*:. okta/*

#java -cp okta-jars/*:. okta/oktaAPI
