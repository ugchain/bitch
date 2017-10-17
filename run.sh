#!/bin/sh

deploylog=`git log --format="deployed version: %h commit by %an, commit message : >>%s<<" -n 1`
echo $(date +'%Y-%m-%d %H:%M:%S')"  "$deploylog >> deploy.log
nohup mvn spring-boot:run > bitch.out &