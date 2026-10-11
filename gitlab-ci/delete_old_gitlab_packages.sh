#!/bin/sh
if [ -z "$CI_JOB_TOKEN" ]; then echo "Missing CI_JOB_TOKEN environment variable" && return 1; fi
if [ -z "$CI_PROJECT_ID" ]; then echo "Missing CI_PROJECT_ID environment variable" && return 1; fi
if [ -z "$PROJECT_PRODUCTION_RELEASE_NAME_TO_KEEP" ]; then echo "Missing PROJECT_PRODUCTION_RELEASE_NAME_TO_KEEP environment variable" && return 1; fi
if [ -z "$REMOVE_PACKAGES_OLDER_THAN_X_DAYS" ]; then echo "Missing REMOVE_PACKAGES_OLDER_THAN_X_DAYS environment variable" && return 1; fi
NB_PACKAGES_PER_PAGE=100
headerToken="JOB-TOKEN:"" $CI_JOB_TOKEN"
removePackagesOlderThan=$(TZ=GMT+$((24*$REMOVE_PACKAGES_OLDER_THAN_X_DAYS)) date +%Y-%m-%d)
echo "Removing packages older that $removePackagesOlderThan from Gitlab's project #$CI_PROJECT_ID"
echo -e "(Release packages with name: '$PROJECT_PRODUCTION_RELEASE_NAME_TO_KEEP', will be kept) \n"
# Reset the file containing the list of packages to delete.
rm -f packages_to_remove.txt
hasNext=true
nextLink="https://gitlab.com/api/v4/projects/$CI_PROJECT_ID/packages?page=1&per_page=$NB_PACKAGES_PER_PAGE&order_by=created_at&sort=asc"
while [ $hasNext == true ]
do
  # Query the Gitlab API to get a list of packages for the given project ID.
  response=$(curl --header "$headerToken" -si -w "\n%{http_code},%{size_header},%{size_download}" "$nextLink")
# Extract the HTTP code.
  httpCode=$(echo "$response" | sed -n '$ s/^\([0-9]*\),.*$/\1/ p')
if [ $httpCode != 200 ]; then echo "Unable to list Gitlab packages, no cleanup can be done" && return 1; fi
# Extract the response header size.
  headerSize=$(echo "$response" | sed -n '$ s/^[0-9]*,\([0-9]*\),.*$/\1/ p')
# Extract the response body size.
  bodySize=$(echo "$response" | sed -n '$ s/^.*,\([0-9]*\)$/\1/ p')
# Extract the response headers.
  headers="${response:0:${headerSize}}"
# Extract the response body.
  body="${response:${headerSize}:${bodySize}}"
echo $body | jq -c '.[] | select ((.name != "'$PROJECT_PRODUCTION_RELEASE_NAME_TO_KEEP'") and (.created_at < "'$removePackagesOlderThan'"))' | jq -r .id >> packages_to_remove.txt
# Extract next page link from response headers.
  nextLink=$(echo "$headers" | grep -oEi '^link:.*<(.*)>;\s+rel="next".*' | sed -E 's/^link:.*<(.*)>;\s+rel="next".*$/\1/')
if [ -z "$nextLink" ]; then
    hasNext=false
  fi
done
# Loop over the list of packages to delete and remove each of them using Gitlab's API.
if [ -s packages_to_remove.txt ]; then
  echo "$(cat packages_to_remove.txt | wc -l) packages will be removed."
cat packages_to_remove.txt | while read PACKAGE_ID
  do
    DELETION_STATUS=$(curl --request DELETE --header "$headerToken" "https://gitlab.com/api/v4/projects/$CI_PROJECT_ID/packages/$PACKAGE_ID" -w '%{http_code}' -s)
    if [ $DELETION_STATUS == 204 ]; then
      echo "Package $PACKAGE_ID has been deleted successfully"
    else
      echo "/!\ Unable to delete package $PACKAGE_ID"
    fi
  done
else
  echo "No package to delete"
fi