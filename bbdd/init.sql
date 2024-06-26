CREATE DATABASE easytakeawaybbdd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;
SET character_set_server=utf8mb4;
SET character_set_client=utf8mb4;

CREATE USER 'AdminEasyTakeAway'@'%' IDENTIFIED BY 'A$19eta!';
GRANT ALL PRIVILEGES ON easytakeawaybbdd.* TO 'AdminEasyTakeAway'@'%';

FLUSH PRIVILEGES;

USE easytakeawaybbdd;

-- Drop tables
DROP TABLE IF EXISTS lineaspedido;
DROP TABLE IF EXISTS envios;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS mesas;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS usuariosinfo;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS roles;

-- Create tables
CREATE TABLE roles (
    rolID INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE usuarios (
    usuarioID INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(100) NOT NULL,
    password CHAR(64) NOT NULL,
    rolID INT NOT NULL,
    CONSTRAINT fk_rol_usuario FOREIGN KEY (rolID) REFERENCES roles(rolID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;;

CREATE TABLE usuariosinfo (
	usuarioID INT PRIMARY KEY,
	nombre VARCHAR(50),
	apellido VARCHAR(50),
	via VARCHAR(10),
	direccion VARCHAR(50),
	numero VARCHAR(15),
	restoDireccion VARCHAR(50),
	ciudad VARCHAR(40),
	codigoPostal VARCHAR(5),
	telefono VARCHAR(15),
	email VARCHAR(100),
	CONSTRAINT fk_usuario FOREIGN KEY (usuarioID) REFERENCES usuarios(usuarioID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE categorias (
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   nombre VARCHAR(100) NOT NULL,
   alias VARCHAR(100) NOT NULL,
   descripcion VARCHAR(250) ,
   imagen VARCHAR(255) DEFAULT 'default.png',
   activado TINYINT NOT NULL,
   padre_id INT,
   todos_padres_ids VARCHAR(250),
   CONSTRAINT fk_padre_categoria FOREIGN KEY (padre_id) REFERENCES categorias(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE UNIQUE INDEX nombre_UNIQUE ON categorias (nombre ASC) VISIBLE;
CREATE INDEX fk_category_category1_idx ON categorias (padre_id ASC) VISIBLE;
CREATE UNIQUE INDEX alias_UNIQUE ON categorias (alias ASC) VISIBLE;

CREATE TABLE productos (
	productoID INT PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(150) NOT NULL,
	alias VARCHAR(200) NULL,
	descripcion LONGTEXT,
	precio DECIMAL(10, 2) NOT NULL,
    iva DECIMAL(2,2) NOT NULL,
	imagen VARCHAR(255) DEFAULT 'default.png',
	categoriaID INT NOT NULL,
	CONSTRAINT fk_categoria FOREIGN KEY (categoriaID) REFERENCES categorias(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX fk_product_category1_idx ON productos (categoriaID ASC) VISIBLE;
CREATE FULLTEXT INDEX productos_FTS ON productos (nombre, descripcion) VISIBLE;

CREATE TABLE mesas (
   mesaID INT AUTO_INCREMENT PRIMARY KEY,
   numero INT NOT NULL,
   capacidad INT NOT NULL,
   posicion VARCHAR(250),
   imagenqr VARCHAR(255)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE pedidos (
   pedidoID INT AUTO_INCREMENT PRIMARY KEY,
   estado ENUM( 'Cancelado', 'Procesado', 'Elaborado', 'Enviado', 'Completado') NOT NULL,
   usuarioID INT NOT NULL,
   tipoenvio INT NOT NULL,
   tipopago INT NOT NULL,
   pagado TINYINT NOT NULL,
   fecha TIMESTAMP,
   importeTotal FLOAT,
   mesaID INT,
   CONSTRAINT fk_usuarioPedido FOREIGN KEY (usuarioID) REFERENCES usuarios(usuarioID),
   CONSTRAINT fk_mesa FOREIGN KEY (mesaID) REFERENCES mesas(mesaID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE envios (
	pedidoId INT PRIMARY KEY NOT NULL,
	direccion VARCHAR (250), 
	ciudad VARCHAR(100),
	codigoPostal VARCHAR(5),
	estado ENUM('Pendiente', 'Cancelado', 'Enviado', 'Espera', 'Entregado') NOT NULL DEFAULT 'Pendiente',
    CONSTRAINT fk_entregas_pedido FOREIGN KEY (pedidoId) REFERENCES pedidos (pedidoId)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE lineaspedido (
	lineaPedidoID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	pedidoID INT,
	productoID INT NOT NULL,
	usuarioID INT NOT NULL,
	cantidad INT,
    precioUnitario DECIMAL(10, 2),
	iva DECIMAL(2,2),
    total DECIMAL(10, 2),
    CONSTRAINT fk_pedido FOREIGN KEY (pedidoID) REFERENCES pedidos(pedidoID),
	CONSTRAINT fk_usuarioLineaPedido FOREIGN KEY (usuarioID) REFERENCES usuarios(UsuarioID),
    CONSTRAINT fk_productoLineaPedido FOREIGN KEY (productoID) REFERENCES productos(ProductoID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- INSERT INICIALES
INSERT INTO roles(Nombre) VALUES ("usuario");
INSERT INTO roles(Nombre) VALUES ("empleado");
INSERT INTO roles(Nombre) VALUES ("administrador");

-- admin/1234
INSERT INTO usuarios (usuarioID, login, password, rolID) VALUES (1, 'admin@email.com', '$2a$10$rLqElIj6qV89/3NmVwAKhuvxbWzyZMjoPf0dBH3Pl.yeQ4rrJmTO2',(SELECT rolID FROM roles WHERE nombre="administrador"));
INSERT INTO usuariosinfo (usuarioID, nombre, apellido, via, direccion, numero, restoDireccion, ciudad, codigoPostal, telefono, email) VALUES (1, 'Administrador', '', '', '', '', '', 'Murcia', '', '111222333', 'administrador@email.com');
-- default/default!13579#
INSERT INTO usuarios (usuarioID, login, password, rolID) VALUES (2, 'default@email.com', '$2a$10$KfNYCCs8jJPEE8VFw1bIHe7IyOop.7u5jgKRZzki7BURLZeH3q/3S',(SELECT rolID FROM roles WHERE nombre="usuario"));
INSERT INTO usuariosinfo (usuarioID, nombre, apellido, via, direccion, numero, restoDireccion, ciudad, codigoPostal, telefono, email) VALUES (2, 'Usuario mesas', '', '', '', '', '', '', '', '', 'default@email.com');
-- usuario/1234
INSERT INTO usuarios (usuarioID, login, password, rolID) VALUES (3, 'usuario@email.com', '$2a$10$rLqElIj6qV89/3NmVwAKhuvxbWzyZMjoPf0dBH3Pl.yeQ4rrJmTO2',(SELECT rolID FROM roles WHERE nombre="usuario"));
INSERT INTO usuariosinfo (usuarioID, nombre, apellido, via, direccion, numero, restoDireccion, ciudad, codigoPostal, telefono, email) VALUES (3, 'Juan antonio', 'García', 'Calle', 'Principal', '1', 'Puerta 1', 'Murcia', '30820', '600777888', 'usuario@email.com');

INSERT INTO categorias (nombre, alias, descripcion, imagen, activado, padre_id, todos_padres_ids) VALUES ('Comida', 'comida', 'Categoria para todo tipo de comida', '1.png', true, null, null);
INSERT INTO categorias (nombre, alias, descripcion, imagen, activado, padre_id, todos_padres_ids) VALUES ('Bebida', 'bebida', 'Categoria para todo tipo de bebida', '2.png', true, null, null);
INSERT INTO categorias (nombre, alias, descripcion, imagen, activado, padre_id, todos_padres_ids) VALUES ('Entrantes', 'entrantes', 'Categoria para los entrantes', '3.png',  true, 1, '-1-');
INSERT INTO categorias (nombre, alias, descripcion, activado, padre_id, todos_padres_ids) VALUES ('Principal', 'principal', 'Categoria para los principales', false, 1, '-1-');
INSERT INTO categorias (nombre, alias, descripcion, imagen, activado, padre_id, todos_padres_ids) VALUES ('Postres', 'postres', 'Categoria para los postres', '5.png',true, 1, '-1-');
INSERT INTO categorias (nombre, alias, descripcion, imagen, activado, padre_id, todos_padres_ids) VALUES ('Hamburgesas', 'hamburguesas', 'Principal', '6.png', true, 4,'-1-4-');
INSERT INTO categorias (nombre, alias, descripcion, imagen, activado, padre_id, todos_padres_ids) VALUES ('Pizzas', 'pizzas', 'Principal', '7.png', true, 4,'-1-4-');

INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Hamburguesa Clásica', 'Classic Burger', 'Deliciosa hamburguesa con carne de res, lechuga, tomate y queso cheddar', 5.99, 0.10, '1.png',  6);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Hamburguesa BBQ', 'BBQ Burger', 'Hamburguesa con salsa BBQ, cebolla caramelizada y queso americano', 6.99, 0.10, '2.png', 6);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Doble de Queso', 'Double Cheeseburger', 'Doble carne de vacuno con doble queso cheddar fundido', 7.49, 0.10, '3.png', 6);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Pizza Margarita', 'Margarita', 'Pizza con salsa de tomate, mozzarella fresca y albahaca', 8.99, 0.10, '4.png', 7);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Pizza Pepperoni', 'Pepperoni', 'Pizza con salsa de tomate, mozzarella y pepperoni', 9.99, 0.10, '5.png', 7);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Pizza Hawaiana', 'Hawaiana', 'Pizza con salsa de tomate, mozzarella, jamón y piña', 10.49, 0.10, '6.png', 7);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Patatas Fritas', 'French Fries', 'Crujientes patatas fritas sazonadas con sal', 3.49, 0.10, '7.png', 3);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Aros de Cebolla', 'Aros', 'Aros de cebolla rebozados y fritos, perfectos para picar', 4.99, 0.10, '8.png', 3);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, categoriaID) VALUES ('Palitos de Queso', 'Palitos', 'Palitos de queso mozzarella rebozados y fritos', 5.49, 0.10, 3);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Refresco de Cola', 'Colacola', 'Refresco de cola fría y refrescante', 1.99, 0.10, '10.png', 2);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Refresco de Naranja', 'Fanta Naranja', 'Refresco de naranja con gas, perfecto para refrescarte', 2.49, 0.10, '11.png', 2);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Agua Mineral', 'Solan de cabras', 'Agua mineral natural en botella', 1.29, 0.10, '12.png', 2);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Cerveza Lager', 'Estrella levante', 'Cerveza Lager fría y refrescante', 2.99, 0.21, '13.png', 2);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, categoriaID) VALUES ('Cerveza IPA', 'Complot IPA Damm', 'Cerveza Indian Pale Ale con un toque amargo y aromático', 3.49, 0.21, 2);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, categoriaID) VALUES ('Vino Tinto', 'Sangre de toro', 'Vino tinto de calidad con cuerpo y sabor afrutado', 12.99, 0.21, 2);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, categoriaID) VALUES ('Vino Blanco', 'Pata Negra Verdejo', 'Vino blanco fresco y ligero con notas cítricas', 10.99, 0.21, 2);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Sidra Natural', 'Cider Maeloc', 'Sidra natural elaborada con manzanas frescas', 4.99, 0.21,  '17.png', 2);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Tarta de Queso', 'Cheesecake', 'Deliciosa tarta de queso horneada con una base de galleta', 5.99, 0.10, '18.png', 5);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Brownie de Chocolate', 'Brownie', 'Brownie de chocolate caliente con helado de vainilla y salsa de chocolate', 6.49, 0.10, '19.png', 5);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Helado de Vainilla', 'Helado de vainilla', 'Tarrina de helado de vainilla cremoso', 3.99, 0.10, '20.png', 5);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Tarta de Manzana', 'Apple Pie', 'Tarta de manzana recién horneada con canela y una crujiente base', 5.49, 0.10, '21.png', 5);
INSERT INTO productos (nombre, alias, descripcion, precio, iva, imagen, categoriaID) VALUES ('Coulant de Chocolate', 'Coulant black', 'Pastel de chocolate caliente con centro líquido y helado de vainilla', 7.99, 0.10, '22.png', 5);