
CREATE TABLE IF NOT EXISTS CONNECTOR_CLIENT_MESSAGE (
	ID INT AUTO_INCREMENT  PRIMARY KEY,
  EBMS_MESSAGE_ID VARCHAR(255),
  BACKEND_MESSAGE_ID VARCHAR(255),
  CONVERSATION_ID VARCHAR(255),
  STORAGE_STATUS VARCHAR(255),
  STORAGE_INFO VARCHAR(255),
  CREATED TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS clientMessageSeq;