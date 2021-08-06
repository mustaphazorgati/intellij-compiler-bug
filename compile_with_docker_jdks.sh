#!/bin/bash

# key is image name, values are image tags
declare -A images=(
  [openjdk]="
    8
    11
    12
    13
    14
    15
    16
    17
  "
  [adoptopenjdk]="
    8-jdk-hotspot
    11-jdk-hotspot
    12-jdk-hotspot
    13-jdk-hotspot
    14-jdk-hotspot
    15-jdk-hotspot
    16-jdk-hotspot

    8-jdk-openj9
    11-jdk-openj9
    12-jdk-openj9
    13-jdk-openj9
    14-jdk-openj9
    15-jdk-openj9
    16-jdk-openj9
  "
)

function main(){
  . "$(dirname "$0")/.utils.sh"

  dockerNames=()

  for image in "${!images[@]}"; do
    for tag in ${images["$image"]}; do
      dockerNames+=( "$image:$tag" )
    done
  done

  set -e # fail when a docker image could not be found
  echo "Downloading docker images..."
  for dockerName in "${dockerNames[@]}"; do
    [[ -z "$(docker images -q "$dockerName")" ]] && docker pull "$dockerName"
  done
  set +e

  longestDockerName=$(getMaxLength "${dockerNames[@]}" "image")

  printRow "$longestDockerName" 20 "image" compiles?


  for dockerName in "${dockerNames[@]}"; do
      log=$(docker run --rm -v "$(realpath "$(dirname "$0")")":/jdk-compiler-bug -v ~/.m2:/root/.m2 -w /jdk-compiler-bug "$dockerName" ./mvnw clean compile)
      mvn_success=$?
      containsError=$(mavenLogContainsError "$log")

      if [[ "$mvn_success" -eq 0 ]]; then
        compiles=yes
      elif [[ "$containsError" == "TRUE" ]]; then
        compiles=no
      else
        compiles="other error"
      fi

      printRow "$longestDockerName" 20 "$dockerName" "$compiles"
    done
}

main "$@"
