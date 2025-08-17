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

### Implementación en DDL

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

- Agregue la columna ```PRECIO_BASE``` a tabla producto (use el mismo tipo de dato que ```DETALLE.PRECIO```), para guardar allí el precio de referencia del producto.

    ```
    ALTER TABLE PRODUCTO
    ADD PRECIO_BASE DOUBLE PRECISION NOT NULL;
    ```

- Agregue la columna ```PRECIO_COSTO``` a tabla ```PRODUCTO``` (use el mismo tipo de dato que ```DETALLE.PRECIO```), para guardar allí el precio de costo del producto.

    ```
    ALTER TABLE PRODUCTO
    ADD PRECIO_COSTO DOUBLE PRECISION NOT NULL;
    ```

- Agregue la columna ```ESTADO``` a tabla ```FACTURA``` (tipo ```SMALLINT```), la cual indica el estado de la factura (0
iniciada, 1 finalizada, 2 anulada).

    ```
    ALTER TABLE FACTURA
    ADD ESTADO SMALLINT CHECK (ESTADO BETWEEN 0 AND 2);
    ```

- Agregue la columna ```FECHA``` a tabla ```FACTURA``` (tipo ```DATE```), la cual indica la fecha de la factura.

    ```
    ALTER TABLE FACTURA
    ADD FECHA DATE NOT NULL;
    ```

En un esquema de implementación B (el usuario/aplicaciones NO interactúa directamente con las tablas), resuelva (agregue los campos o tablas que considere necesarios):

1. Cada vez que se vende un producto, se descuenta su cantidad de stock (cada vez que deja de vender un producto, sume su cantidad al stock). Un producto no puede venderse si el stock no es suficiente.

    ### Trigger BIDETALLE

    Este trigger verifica si la cantidad que se desea insertar en la tabla ```DETALLE``` es válida.

    Si el stock del producto es menor a la cantidad a insertar, genera una excepción.

    ```
    CREATE EXCEPTION EX_STOCK_INSUFICIENTE 'No hay stock suficiente para la cantidad deseada.';

    SET TERM ^ ;

    CREATE TRIGGER TRG_BIDETALLE FOR DETALLE
    ACTIVE BEFORE INSERT POSITION 0
    AS
        DECLARE VARIABLE STOCK_DISPONIBLE TYPE OF COLUMN PRODUCTO.STOCK;
    BEGIN
        SELECT STOCK FROM PRODUCTO WHERE ID = NEW.ID
        INTO :STOCK_DISPONIBLE;
        IF(:STOCK_DISPONIBLE - NEW.CANTIDAD < 0) THEN
            EXCEPTION EX_STOCK_INSUFICIENTE;
    END^

    SET TERM ; ^
    ```

    ### Procedure AGREGAR_PRODUCTO_DETALLE

    Creo un stored procedure ```AGREGAR_PRODUCTO_DETALLE```, porque luego se va a utilizar para otros triggers.

    Lo que hace el procedure es descontar del stock del producto la cantidad insertada en la tupla de ```DETALLE``` y actualizar el importe de ```FACTURA```.

    ```
    SET TERM ^ ;
    CREATE PROCEDURE AGREGAR_PRODUCTO_DETALLE
    (
        NRO TYPE OF COLUMN DETALLE.NRO,
        ID TYPE OF COLUMN DETALLE.ID,
        CANTIDAD TYPE OF COLUMN DETALLE.CANTIDAD,
        PRECIO TYPE OF COLUMN DETALLE.PRECIO
    )
    AS
    BEGIN
        UPDATE PRODUCTO SET STOCK = STOCK - :CANTIDAD WHERE ID = :ID;
        UPDATE FACTURA SET IMPORTE = IMPORTE + (:PRECIO * :CANTIDAD) WHERE NRO = :NRO;
    END ^
    SET TERM ; ^
    ```

    ### Trigger AIDETALLE

    Este trigger se dispara si el trigger ```BIDETALLE``` no arroja una excepción, es decir que la cantidad de producto a insertar en la tabla ```DETALLE``` es válida.

    ```
    SET TERM ^ ;
    CREATE TRIGGER TRG_AIDETALLE FOR DETALLE
    ACTIVE AFTER insert POSITION 0
    AS
    BEGIN
        EXECUTE PROCEDURE AGREGAR_PRODUCTO_DETALLE(NEW.NRO, NEW.ID, NEW.CANTIDAD, NEW.PRECIO);
    END^
    SET TERM ; ^
    ```

    ### Procedure ELIMINAR_PRODUCTO_DETALLE

    En este caso también creo un stored procedure ```ELIMINAR_PRODUCTO_DETALLE```, que se va a volver a usar más adelante.

    Lo que hace el procedure ```ELIMINAR_PRODUCTO_DETALLE``` es sumar la cantidad de la tupla eliminada en ```DETALLE``` al stock del producto y actualizar el importe de ```FACTURA```.

    ```
    SET TERM ^ ;
    CREATE PROCEDURE ELIMINAR_PRODUCTO_DETALLE
    (
        NRO TYPE OF COLUMN DETALLE.NRO,
        ID TYPE OF COLUMN DETALLE.ID,
        CANTIDAD TYPE OF COLUMN DETALLE.CANTIDAD,
        PRECIO TYPE OF COLUMN DETALLE.PRECIO
    )
    AS
    BEGIN
        UPDATE PRODUCTO SET STOCK = STOCK + :CANTIDAD WHERE ID = :ID;
        UPDATE FACTURA SET IMPORTE = IMPORTE - (:PRECIO * :CANTIDAD) WHERE NRO = :NRO;
    END^
    SET TERM ; ^
    ```

    ### Trigger ADDETALLE

    Este trigger se dispara cuando se elimina una tupla de ```DETALLE```. 

    ```
    SET TERM ^ ;
    CREATE TRIGGER TRG_ADDETALLE FOR DETALLE 
    ACTIVE AFTER delete POSITION 0
    AS
    BEGIN
        EXECUTE PROCEDURE ELIMINAR_PRODUCTO_DETALLE(OLD.NRO, OLD.ID, OLD.CANTIDAD, OLD.PRECIO);
    END ^
    SET TERM ; ^
    ```

2. El importe de la factura es igual a la sumatoria de cantidad * precio; actualice el importe de la factura a medida que vende o deja de vender productos.

    Esto se hace en los stored procedures ```AGREGAR_PRODUCTO_DETALLE``` y ```ELIMINAR_PRODUCTO_DETALLE```.

    ### Trigger BIFACTURA

    El trigger ```BIFACTURA``` setea por default la fecha de la factura al día actual, y al importe y al estado en 0. Si no se seteara el importe en 0, no funcionarian los stored procedres ```AGREGAR_PRODUCTO_DETALLE``` y ```ELIMINAR_PRODUCTO_DETALLE.```.

    ```
    SET TERM ^ ;
    CREATE TRIGGER TRG_BIFACTURA FOR FACTURA 
    ACTIVE BEFORE insert POSITION 0
    AS
    BEGIN
        IF (NEW.FECHA IS NULL) THEN NEW.FECHA = 'TODAY';
        NEW.IMPORTE = 0;
        NEW.ESTADO = 0;
    END ^
    SET TERM ; ^
    ```

3. Un producto no puede venderse a un precio por debajo de su precio base.

    ### ALTER Trigger BIDETALLE

    Para controlar que no se venda un producto a un precio por debajo de su precio base, hay que modificar el trigger ```BIDETALLE```. Antes creo un exception que se dispara cuando se vende un producto a un precio menor. Además, en el trigger también agrego que cuando no se especifica el precio de venta se tome el valor de ```PRECIO_BASE``` en ```PRODUCTO```.

    ```
    CREATE EXCEPTION EX_PRECIO_INVALIDO 'El precio de venta de los productos no pueden ser menor al precio base del producto.';

    SET TERM ^ ;
    ALTER TRIGGER TRG_BIDETALLE 
    ACTIVE BEFORE insert POSITION 0
    AS
        DECLARE VARIABLE PRECIO_BASE TYPE OF COLUMN PRODUCTO.PRECIO_BASE;
        DECLARE VARIABLE STOCK_DISPONIBLE TYPE OF COLUMN PRODUCTO.STOCK;
    BEGIN
        SELECT PRECIO_BASE FROM PRODUCTO WHERE ID = NEW.ID INTO :PRECIO_BASE;
        IF (NEW.PRECIO IS NULL) THEN
            NEW.PRECIO = :PRECIO_BASE;
        IF (NEW.PRECIO < :PRECIO_BASE) THEN
            EXCEPTION EX_PRECIO_INVALIDO;
        SELECT STOCK FROM PRODUCTO WHERE ID = NEW.ID
        INTO :STOCK_DISPONIBLE;
        IF(:STOCK_DISPONIBLE - NEW.CANTIDAD < 0) THEN
            EXCEPTION EX_STOCK_INSUFICIENTE;
    END ^
    SET TERM ; ^
    ```

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