#!/bin/sh

curl -u bamboo:bamboo2997 -H "SOAPAction: foo" -H "Content-Type: text/xml;charset=UTF-8" -d @messages/login.xml -X POST http://localhost:1990/jira/rpc/soap/agilossoapservice-v1