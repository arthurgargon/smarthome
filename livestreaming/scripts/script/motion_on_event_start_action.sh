#!/bin/bash
# send motion event to clunet
wget --timeout=5 --quiet "http://192.168.1.20:8000/send?dst=255&cmd=65&pri=4&dat=0100"
