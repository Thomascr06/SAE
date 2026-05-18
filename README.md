# SAE
Achille LEGRAND, Thomas CROIZYEUKS, Paul ROSSIGNOL

### Basic
- [ ] **GET /grids**: List all grid IDs ✅
  *Utilise la méthode db.createNativeQuery() dans GridHandler pour récupérer les IDs des réseaux.*
- [ ] **GET /grid/{id}**: Get grid details ✅
  *Utilise la méthode db.find(Grid.class, id) dans GridHandler pour récupérer les détails d'un réseau.*
- [ ] **GET /persons**: List all person IDs ✅
  *Utilise la méthode db.createNativeQuery() dans PersonHandler pour récupérer les IDs des personnes.*
- [ ] **GET /person/{id}**: Get person by ID ✅
  *Utilise la méthode db.find(Person.class, id) dans PersonHandler pour récupérer une personne.*
- [ ] **GET /measurement/{id}**: Get measurement definition ✅
  *Utilise la méthode db.find(Measurement.class, id) dans MeasurementHandler pour récupérer une mesure.*

### Medium
- [ ] **PUT /person**: Create a new person ✅
  *Utilise la méthode db.persist(person) dans PersonHandler pour ajouter une nouvelle personne.*
- [ ] **POST /person/{id}**: Update person values ✅
  *Utilise la méthode db.merge(person) dans PersonHandler pour modifier une personne existante.*
- [ ] **DELETE /person/{id}**: Delete a person ✅
  *Utilise la méthode db.remove(person) dans PersonHandler pour supprimer une personne.*
- [ ] **GET /sensors/{kind}**: List sensors of a given kind ✅
  *Utilise la méthode db.createNativeQuery() dans SensorHandler avec la clause dtype pour filtrer les capteurs par type.*
- [ ] **GET /producers**: List all producers ✅
  *Utilise la méthode db.createQuery() dans ProducerHandler pour récupérer les producteurs.*
- [ ] **GET /consumers**: List all consumers ✅
  *Utilise la méthode db.createQuery() dans ConsumerHandler pour récupérer les consommateurs.*

### Advanced
- [ ] **POST /ingress/windturbine**: Receive wind turbine measurement ✅
  *Utilise la méthode db.persist(measurement) dans IngressHandler pour enregistrer les données d'une éolienne.*
- [ ] **POST /ingress/solarpanel**: Receive solar panel measurement ✅
  *Utilise la méthode db.persist(measurement) dans IngressHandler pour enregistrer les données d'un panneau solaire.*
- [ ] **GET /sensor/{id}**: Get sensor detail ✅
  *Utilise la méthode db.find(Sensor.class, id) dans SensorHandler pour récupérer les informations d'un capteur.*
- [ ] **POST /sensor/{id}**: Update a sensor ✅
  *Utilise la méthode db.merge(sensor) dans SensorHandler pour mettre à jour les données d'un capteur.*
- [ ] **GET /measurement/{id}/values**: Get measurement values

### Hard
- [ ] **GET /grid/{id}/production**: Get grid total production
- [ ] **GET /grid/{id}/consumption**: Get grid total consumption

