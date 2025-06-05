CREATE DATABASE qa_api;
USE qa_api;

CREATE TABLE api_tests (
  id INT AUTO_INCREMENT PRIMARY KEY,
  method VARCHAR(6) NOT NULL,            -- "GET" or "POST"
  endpoint VARCHAR(255) NOT NULL,         -- full URL or path
  payload JSON,                           -- JSON body (for POST)
  expected_status INT NOT NULL,           -- e.g., 200, 404
  expected_json_path VARCHAR(255) NULL,   -- JSONPath to check (e.g. "$.data.id")
  expected_value VARCHAR(255) NULL        -- fragment to look for
);

INSERT INTO api_tests (method, endpoint, payload, expected_status, expected_json_path, expected_value)
  VALUES
    ('GET',  'https://jsonplaceholder.typicode.com/posts/1', NULL, 200, '$.id', '1'),
    ('GET',  'https://jsonplaceholder.typicode.com/posts/99999', NULL, 404, NULL, NULL),
    ('POST', 'https://jsonplaceholder.typicode.com/posts',
           '{"title":"foo","body":"bar","userId":1}', 201, '$.title', 'foo');
