
#Create pvcs first, then mongo, redis, then app and then cronjob
cd Kubernetes
kubectl apply -f MongoPersistentVolumeClaim.yaml -f PersistentVolumeClaim.yaml -f Redis.yaml -f Mongo.yaml -f Backend.yaml #-f Cronjob.yaml #-f MongoExpress.yaml
cd ..
