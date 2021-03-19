double shippingCost = 0.0;
switch(orderSize) {
        case 15:
            shippingCost = 3.99;
            break;
        case 10:
            shippingCost = 2.99;
            break;
        case 5:
            shippingCost = 1.99;
            break;
        default:
            shippingCost = 5.99;
    }
System.out.println(shippingCost);
