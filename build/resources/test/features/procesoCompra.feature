Feature: Flujo de compra pagina OpenCart

  @compraOpenCart
  Scenario: Flujo de compra completo en OpenCart
    Given El usuario abre la página openCart con los datos de prueba "openCartCompra" y el caso "1"
      | data |
      | data |
    When realiza la compra de dos productos y realiza checkout con la informacion requerida
    Then Deberia ver el mensaje de confirmacion de la compra exitosa

