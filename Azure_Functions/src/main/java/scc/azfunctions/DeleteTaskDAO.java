package src.main.java.scc.serverless;

import java.util.Objects;

public class DeleteTaskDAO {
        private String id;
        private String taskType; //DeleteUser, DeleteHouse
        private String entityId;

        public DeleteTaskDAO(String id, String taskType, String entityId) {
            this.id = id;
            this.taskType = taskType;
            this.entityId = entityId;
        }

        public DeleteTaskDAO() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTaskType() {
            return taskType;
        }

        public void setTaskType(String taskType) {
            this.taskType = taskType;
        }

        public String getEntityId() {
            return entityId;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        @Override
        public String toString() {
            return "DeleteTaskDAO{" +
                    "id='" + id + '\'' +
                    ", taskType='" + taskType + '\'' +
                    ", entityId='" + entityId + '\'' +
                    '}';
        }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteTaskDAO that = (DeleteTaskDAO) o;
        return Objects.equals(id, that.id) && Objects.equals(taskType, that.taskType) && Objects.equals(entityId, that.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskType, entityId);
    }

}
