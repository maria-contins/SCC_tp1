

#Create directory for test


# Load data with artillery


cd ./tests

artillery run -t $APPLICATION_URL load-data.yml


#run workload1
artillery run -t $APPLICATION_URL -o report.json workload1.yml

#run artillery report
artillery report report.json


mkdir -p test_result

mv *.json test_result
mv *.html test_result


#upload full folder to transfer.sh

zip -r test_result.zip test_result

#clear
echo -e "----------------------------\n"
echo "Test result is available at:"

curl -H "Max-Downloads: 2" -H "Max-Days: 1" --upload-file ./test_result.zip https://transfer.sh/test_result.zip 

echo -e "\n----------------------------\n"

#sleep 30

exit 0



