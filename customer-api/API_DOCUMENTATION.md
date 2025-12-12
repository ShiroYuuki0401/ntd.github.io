# Customer API Documentation

## Base URL
`http://localhost:8080/api/customers`

---

## Endpoints

### 1. Get All Customers (Paginated & Sorted)
Retrieves a paginated list of customers.  

- **Method:** `GET`  
- **URL:** `/api/customers`  
- **Query Parameters:**

| Parameter | Type    | Default | Description                           |
|-----------|---------|---------|---------------------------------------|
| page      | integer | 0       | Page number (0-indexed)               |
| size      | integer | 10      | Number of items per page              |
| sortBy    | string  | id      | Field to sort by (e.g., fullName)     |
| sortDir   | string  | asc     | Sort direction (`asc` or `desc`)      |

**Response: 200 OK**
```json
{
  "customers": [
    {
      "id": 1,
      "customerCode": "C001",
      "fullName": "John Doe",
      "email": "john@example.com",
      "phone": "+1-555-0101",
      "address": "123 Main St",
      "status": "ACTIVE",
      "createdAt": "2023-11-01T10:00:00"
    }
  ],
  "currentPage": 0,
  "totalItems": 1,
  "totalPages": 1
}
```

---

### 2. Get Customer by ID
Retrieves a specific customer by their unique ID.  

- **Method:** `GET`  
- **URL:** `/api/customers/{id}`  

**Response: 200 OK**
```json
{
  "id": 1,
  "customerCode": "C001",
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "+1-555-0101",
  "address": "123 Main St",
  "status": "ACTIVE",
  "createdAt": "2023-11-01T10:00:00"
}
```

---

### 3. Create Customer
Creates a new customer.  

- **Method:** `POST`  
- **URL:** `/api/customers`  
- **Headers:** `Content-Type: application/json`  
- **Body:**
```json
{
  "customerCode": "C005",
  "fullName": "Jane Doe",
  "email": "jane@example.com",
  "phone": "+1-555-0000",
  "address": "123 Street"
}
```

**Response: 201 Created**
```json
{
  "id": 5,
  "customerCode": "C005",
  "fullName": "Jane Doe",
  "email": "jane@example.com",
  "phone": "+1-555-0000",
  "address": "123 Street",
  "status": "ACTIVE",
  "createdAt": "2023-11-10T14:30:00"
}
```

---

### 4. Update Customer (Full)
Updates an existing customer's information (replaces all mutable fields).  

- **Method:** `PUT`  
- **URL:** `/api/customers/{id}`  
- **Headers:** `Content-Type: application/json`  
- **Body:**
```json
{
  "customerCode": "C005",
  "fullName": "Jane Doe Updated",
  "email": "jane.updated@example.com",
  "phone": "+1-555-9999",
  "address": "456 New Ave"
}
```

**Response: 200 OK**
```json
{
  "id": 5,
  "customerCode": "C005",
  "fullName": "Jane Doe Updated",
  "email": "jane.updated@example.com",
  "phone": "+1-555-9999",
  "address": "456 New Ave",
  "status": "ACTIVE",
  "createdAt": "2023-11-10T14:30:00"
}
```

---

### 5. Partial Update (PATCH)
Updates only specific fields of a customer.  

- **Method:** `PATCH`  
- **URL:** `/api/customers/{id}`  
- **Headers:** `Content-Type: application/json`  
- **Body:**
```json
{
  "phone": "+1-999-9999",
  "address": "789 Partial St"
}
```

**Response: 200 OK**
```json
{
  "id": 5,
  "customerCode": "C005",
  "fullName": "Jane Doe Updated",
  "email": "jane.updated@example.com",
  "phone": "+1-999-9999",
  "address": "789 Partial St",
  "status": "ACTIVE",
  "createdAt": "2023-11-10T14:30:00"
}
```

---

### 6. Delete Customer
Removes a customer from the system.  

- **Method:** `DELETE`  
- **URL:** `/api/customers/{id}`  

**Response: 200 OK**
```json
{
  "message": "Customer deleted successfully"
}
```

---

### 7. Search Customers
Search for customers by keyword (matches Name, Email, or Customer Code).  

- **Method:** `GET`  
- **URL:** `/api/customers/search?keyword={value}`  

**Response: 200 OK**
```json
[
  {
    "id": 1,
    "customerCode": "C001",
    "fullName": "John Doe",
    "email": "john@example.com",
    "status": "ACTIVE"
  }
]
```

---

### 8. Filter by Status
Filter customers by their status (`ACTIVE` or `INACTIVE`).  

- **Method:** `GET`  
- **URL:** `/api/customers/status/{status}`  

**Response: 200 OK**
```json
[
  {
    "id": 2,
    "customerCode": "C002",
    "fullName": "Alice Smith",
    "email": "alice@example.com",
    "status": "INACTIVE"
  }
]
```

---

### 9. Advanced Search
Filter using multiple optional criteria.  

- **Method:** `GET`  
- **URL:** `/api/customers/advanced-search`  
- **Query Params:** `name`, `email`, `status` (all optional)  
- **Example:** `/api/customers/advanced-search?name=john&status=ACTIVE`  

**Response: 200 OK**
```json
[
  {
    "id": 1,
    "customerCode": "C001",
    "fullName": "John Doe",
    "email": "john@example.com",
    "status": "ACTIVE"
  }
]
```

---

## Status Code & Error Examples

### 400 Bad Request (Validation Error)
Occurs when input data fails validation.  
```json
{
  "timestamp": "2023-11-10T15:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/customers",
  "details": [
    "email: Invalid email format",
    "fullName: Name must be 2-100 characters"
  ]
}
```

### 404 Not Found
Occurs when the requested resource ID does not exist.  
```json
{
  "timestamp": "2023-11-10T15:05:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 999",
  "path": "/api/customers/999",
  "details": null
}
```

### 409 Conflict
Occurs when trying to create/update a resource with unique constraints.  
```json
{
  "timestamp": "2023-11-10T15:10:00",
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists: john@example.com",
  "path": "/api/customers",
  "details": null
}
```

### 500 Internal Server Error
Occurs when an unexpected server error happens.  
```json
{
  "timestamp": "2023-11-10T15:15:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Connection refused...",
  "path": "/api/customers",
  "details": null
}
```