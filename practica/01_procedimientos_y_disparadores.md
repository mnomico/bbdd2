## [Volver atrás](../readme.md)

<div align="center">
<h1>Procedimientos y Disparadores</h1>
</div>

Dadas las siguientes tablas mencionadas:

```
FACTURA(NRO,IMPORTE)
DETALLE(NRO,ID,CANTIDAD,PRECIO)
    NRO FK FACTURA
    ID FK PRODUCTO
PRODUCTO(ID,DESCR,STOCK)
```

### Implementación en DDL:

```
CREATE TABLE FACTURA
(
  NRO INTEGER NOT NULL PRIMARY KEY,
  IMPORTE DOUBLE PRECISION NOT NULL
);

CREATE TABLE PRODUCTO
(
    ID INTEGER NOT NULL PRIMARY KEY,
    DESCR VARCHAR(50),
    STOCK INTEGER
);

CREATE TABLE DETALLE
(
    NRO INTEGER NOT NULL,
    ID INTEGER NOT NULL,
    CANTIDAD INTEGER NOT NULL,
    PRECIO DOUBLE PRESICION NOT NULL,
    CONSTRAINT PK_DETALLE PRIMARY KEY(NRO, ID),
    CONSTRAINT FK_DETALLE_FACTURA FOREIGN KEY(NRO) REFERENCES FACTURA,
    CONSTRAINT FK_DETALLE_PRODUCTO FOREIGN KEY(ID) REFERENCES PRODUCTO
);
```

- Agregue la columna PRECIO_BASE a tabla producto (use el mismo tipo de dato que DETALLE.PRECIO), para guardar allí el precio de referencia del producto.

```
ALTER TABLE PRODUCTO
ADD PRECIO_BASE DOUBLE PRECISION NOT NULL;
```

- Agregue la columna PRECIO_COSTO a tabla producto (use el mismo tipo de dato que DETALLE.PRECIO), para guardar allí el precio de costo del producto.

```
ALTER TABLE PRODUCTO
ADD PRECIO_COSTO DOUBLE PRECISION NOT NULL;
```

- Agregue la columna ESTADO a tabla factura (tipo SMALLINT), la cual indica el estado de la factura (0
iniciada, 1 finalizada, 2 anulada).

```
ALTER TABLE FACTURA
ADD ESTADO SMALLINT CHECK (ESTADO BETWEEN 0 AND 2);
```

- Agregue la columna FECHA a tabla factura (tipo DATE), la cual indica la fecha de la factura.

```
ALTER TABLE FACTURA
ADD FECHA DATE NOT NULL;
```

En un esquema de implementación B (el usuario/aplicaciones NO interactúa directamente con las tablas), resuelva (agregue los campos o tablas que considere necesarios):

1. Cada vez que se vende un producto, se descuenta su cantidad de stock (cada vez que deja de vender un producto, sume su cantidad al stock). Un producto no puede venderse si el stock no es suficiente.

2. El importe de la factura es igual a la sumatoria de cantidad * precio; actualice el importe de la factura a medida que vende o deja de vender productos.

3. Un producto no puede venderse a un precio por debajo de su precio base.

4. El estado de una factura comienza con estado 0 (iniciada).

    De estado 0 iniciada, puede pasar a estado 1 finalizada.

    De estado 0 iniciada, puede pasar a estado 2 anulada.

    De estado 1 finalizada, puede pasar a estado 2 anulada, pero no puede volver a estado 0 iniciada.
    
    De estado 2 anulada, puede pasar a estado 1 finalizada, pero no puede volver a estado 0 iniciada.

    Si una factura pasa de estado 1 finalizada a estado 2 anulada, se debe devolver toda la cantidad de producto al stock.

    Si una factura estaba en estado 2 anulada y pasa a estado 1 finalizada, se debe restar toda la cantidad de producto del stock.

5. Implemente, de forma genérica, para todas las facturas, el siguiente control: por ejemplo, si la factura 100 tiene fecha 02-SEP-14, entonces, la factura 101 deberá tener una fecha mayor igual al 02-SEP-14 y nunca menor.

### Implementación de Triggers:

<div align="center">

![](/practica/imagenes/01_tabla01.png)

</div>