package ru.clevertec.check.service.impl;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.clevertec.check.dao.OrderRepository;
import ru.clevertec.check.dao.ProductRepository;
import ru.clevertec.check.dao.UserRepository;
import ru.clevertec.check.dao.WarehouseRepository;
import ru.clevertec.check.model.order.DataOrder;
import ru.clevertec.check.model.product.Card;
import ru.clevertec.check.model.product.Order;
import ru.clevertec.check.model.product.Product;
import ru.clevertec.check.model.product.SingleProduct;
import ru.clevertec.check.model.user.User;
import ru.clevertec.check.service.CheckService;
import ru.clevertec.check.service.MailService;
import ru.clevertec.check.service.PrintService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static ru.clevertec.check.service.CheckConstants.*;

@AllArgsConstructor
@Service
public class CheckServiceImpl implements CheckService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final PrintService printService;
    private final Gson gson;

    @Override
    public StringBuilder getTXT(Map<String, Integer> map, Integer id) {
        StringBuilder sb = new StringBuilder();
        Order order = getOrder(map);
        saveDataOrder(map, id);
        sb.append("\n\n              CASH RECEIPT\n\n").
                append("       supermarket 'The Two Geese' \n").
                append("      " + new Date().toString() + "\n\n").
                append("QTY  DESCRIPTION            PRICE   TOTAL \n");
        sb.append(buildSingleProducts(map, OUTPUT_TXT));
        sb.append(TRANSFER);
        sb.append(getCardTXT(order.getCard().getNumber(),
                order.getTotalPrice(), order.getDiscount()));
        return sb;
    }

    @Override
    public StringBuilder getHTML(Map<String, Integer> map) {
        Order order = getOrder(map);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HTML_OPEN).
                append("<head><h2><pre>        CASH RECEIPT</pre></h2></head>").
                append("<pre>        supermarket 'The Two Geese'</pre> ").
                append(String.format("<pre>       %s </pre>    ", new Date().toString())).
                append("<h3><pre>QTY DESCRIPTION          PRICE   TOTAL </pre></h3>");
        stringBuilder.append(buildSingleProducts(map, OUTPUT_HTML));
        stringBuilder.append(TRANSFER_HTML).
                append(getCardHTML(order.getCard().getNumber(), order.getTotalPrice(), order.getDiscount())).
                append(HTML_CLOSE);
        return stringBuilder;
    }

    @Override
    public StringBuilder getPDF(Map<String, Integer> map) {
        Order order = getOrder(map);
        StringBuilder sb = new StringBuilder();
        sb.append(DOUBLE_INDENT + TRANSPORT + TRANSPORT_HALF + "      CASH RECEIPT" + DOUBLE_INDENT).
                append(TRANSPORT + TRANSPORT_HALF + "  supermarket 'The Two Geese' \n").
                append(TRANSPORT + TRANSPORT_HALF + new Date().toString() + DOUBLE_INDENT).
                append(TRANSPORT + "QTY        DESCRIPTION            PRICE         TOTAL \n");
        sb.append(buildSingleProducts(map, OUTPUT_PDF));
        sb.append(TRANSPORT + TRANSFER_PDF);
        sb.append(getCardPDF(order.getCard().getNumber(), order.getTotalPrice(), order.getDiscount()));
        return sb;
    }

    @SneakyThrows
    @Override
    public void getPDFFromOrder(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append(DOUBLE_INDENT + TRANSPORT + TRANSPORT_HALF + "      CASH RECEIPT" + DOUBLE_INDENT).
                append(TRANSPORT + TRANSPORT_HALF + "  supermarket 'The Two Geese' \n").
                append(TRANSPORT + TRANSPORT_HALF + order.getDate() + DOUBLE_INDENT).
                append(TRANSPORT + "QTY        DESCRIPTION            PRICE         TOTAL \n");
        for (SingleProduct product : order.getProducts()) {
            sb.append(String.format(OUTPUT_PDF, product.getQuantity(), product.getName(),
                    product.getPrice(), product.getTotalPrice()));
        }
        sb.append(TRANSPORT + TRANSFER_PDF);
        sb.append(getCardPDF(order.getCard().getNumber(), order.getTotalPrice(), order.getDiscount()));
        printService.printPDFCheck(sb);
    }

    @Override
    public Order getOrder(Map<String, Integer> map) {
        List<SingleProduct> products = getSingleProducts(map);
        Card card = null;
        double totalPriceWODiscount = ZERO_INT;
        double cardPercent = ZERO_INT;
        double totalPrice = ZERO_INT;
        double totalDiscount;
        Order order = new Order();
        for (Map.Entry<String, Integer> entry : map.entrySet()
        ) {
            if (entry.getKey().equals(CARD)) {
                card = new Card(entry.getValue());
                cardPercent = getPercent(card);
            } else {
                card = new Card(CARD_RANGE_0);
                cardPercent = ONE_INT;
            }
        }
        for (SingleProduct product : products) {
            totalPriceWODiscount += product.getQuantity() * product.getPrice();
            totalPrice += product.getTotalPrice();
        }
        totalPrice = totalPrice * cardPercent;
        totalDiscount = totalPriceWODiscount - totalPrice;
        order.setProducts(products);
        order.setCard(card);
        order.setCardPercent(cardPercent);
        order.setDiscount(totalDiscount);
        order.setTotalPrice(totalPrice);
        return order;
    }

    @Override
    public Map<String, Integer> selectProducts(HttpServletRequest request) {
        List<Product> products = productRepository.findAll();
        Map<String, Integer> purchaseParameters = new HashMap<>();
        for (Product item : products) {
            if (Objects.nonNull(request.getParameter(item.getName())) &&
                    !request.getParameter(item.getName()).isBlank()) {
                purchaseParameters.put(item.getName(), Integer.parseInt(request.getParameter(item.getName())));
            }
        }
        if (request.getParameter(CARD) != null && !request.getParameter(CARD).isBlank()) {
            purchaseParameters.put(CARD, Integer.parseInt(request.getParameter(CARD)));
        } else {
            purchaseParameters.put(CARD, ZERO_INT);
        }
        return purchaseParameters;
    }

    @Override
    public Order sendCheckToEmail(Integer id) {
        User user = userRepository.getUserById(id);
        List<DataOrder> orders = orderRepository.getDataOrdersByUserId(id);
        getPDFFromOrder(gson.fromJson(orders.get(orders.size() - ONE_INT).getJson(), Order.class));
        mailService.send(user.getEmail(), CHECKFILEPDF);
        return gson.fromJson(orders.get(orders.size() - ONE_INT).getJson(), Order.class);
    }

    private void saveDataOrder(Map<String, Integer> map, Integer id) {
        DataOrder dataOrder = new DataOrder();
        Order order = getOrder(map);
        dataOrder.setUserId(id);
        dataOrder.setJson(gson.toJson(order));
        orderRepository.save(dataOrder);
    }

    private List<SingleProduct> getSingleProducts(Map<String, Integer> map) {
        List<Product> list = productRepository.findAll();
        SingleProduct singleProduct;
        double totalPriceProduct;
        List<SingleProduct> products = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()
        ) {
            if (!entry.getKey().equals(CARD) && entry.getValue() != ZERO_INT) {
                for (int i = ZERO_INT; i < list.size(); i++) {
                    String key = entry.getKey();
                    if (key.equals(list.get(i).getName())) {
                        int quantity = entry.getValue();
                        totalPriceProduct = list.get(i).getCost() * quantity;
                        singleProduct = new SingleProduct(quantity, list.get(i).getName(),
                                list.get(i).getCost(), totalPriceProduct, list.get(i));
                        if (singleProduct.getProduct().getName() != null)
                            products.add(singleProduct);
                    }
                }
            }
        }

        return getStockPrice(products);
    }

    private StringBuilder buildSingleProducts(Map<String, Integer> map, String format) {
        List<Product> list = productRepository.findAll();
        StringBuilder sb = new StringBuilder();
        String line = null;
        double totalPriceProduct;
        double totalPrice = ZERO_INT;
        double discount = ZERO_INT;
        for (Map.Entry<String, Integer> entry : map.entrySet()
        ) {
            if (!entry.getKey().equals(CARD)) {
                for (Product product : list) {
                    String key = entry.getKey();
                    int quantity = entry.getValue();
                    Integer repositoryQuantity = warehouseRepository.getQuantity(key);
                    if (product.getName().equalsIgnoreCase(key) && quantity <= repositoryQuantity) {
                        if (product.isStock() && quantity >= PRODUCT_NUMBER) {
                            totalPriceProduct = product.getCost() * quantity * PERCENT90;
                        } else {
                            totalPriceProduct = product.getCost() * quantity;
                        }
                        line = String.format(format, quantity,
                                product.getName(), product.getCost(), totalPriceProduct);
                        totalPrice += totalPriceProduct;
                        discount += product.getCost() * quantity - totalPriceProduct;
                        warehouseRepository.updateQuantity(repositoryQuantity - quantity, key);
                        sb.append(line);
                    } else if (product.getName().equalsIgnoreCase(key) && quantity > repositoryQuantity) {
                        if (product.isStock() && quantity >= PRODUCT_NUMBER) {
                            totalPriceProduct = product.getCost() * repositoryQuantity * PERCENT90;
                        } else {
                            totalPriceProduct = product.getCost() * repositoryQuantity;
                        }
                        line = String.format(format, repositoryQuantity,
                                product.getName(), product.getCost(), totalPriceProduct);
                        totalPrice += totalPriceProduct;
                        discount += product.getCost() * quantity - totalPriceProduct;
                        warehouseRepository.updateQuantity(ZERO_INT, key);
                        sb.append(line);
                    }
                }
            } else {
                new Card(entry.getValue());
            }
        }
        return sb;
    }

    private double getPercent(Card card) {
        int number = card.getNumber();
        if (number == CARD_RANGE_0) {
            return ONE_INT;
        } else {
            if (number > CARD_RANGE_0 && number < CARD_RANGE_100) {
                return PERCENT97;
            } else if (number >= CARD_RANGE_100 && number < CARD_RANGE_1000) {
                return PERCENT96;
            } else if (number >= CARD_RANGE_1000) {
                return PERCENT95;
            }
        }
        return ONE_INT;
    }

    private List<SingleProduct> getStockPrice(List<SingleProduct> list) {
        List<SingleProduct> discountedProducts = new ArrayList<>();
        for (SingleProduct product : list) {
            if (product.getQuantity() >= 5 && product.getProduct().isStock()) {
                product.setTotalPrice(product.getTotalPrice() * PERCENT90);
                discountedProducts.add(product);
            } else {
                discountedProducts.add(product);
            }
        }
        return discountedProducts;
    }

    private StringBuilder getCardHTML(int card, double totalPrice, double discount) {
        StringBuilder stringBuilder = new StringBuilder();
        if (card == CARD_RANGE_0) {
            stringBuilder.append("<pre>No Discount Card </pre>");
        } else {
            if (card > CARD_RANGE_0 && card < CARD_RANGE_100) {
                stringBuilder.append("<pre>Your Card with 3% Discount: </pre>" + card);
            } else if (card >= CARD_RANGE_100 && card < CARD_RANGE_1000) {
                stringBuilder.append("<pre>Your Card with 4% Discount: </pre>" + card);
            } else if (card >= CARD_RANGE_1000) {
                stringBuilder.append("<pre>Your Card with 5% Discount: </pre>" + card);
            }
        }
        stringBuilder.append(String.format(DISCOUNT_HTML, discount)).
                append(String.format(PRICE_HTML, totalPrice));
        return stringBuilder;
    }

    private StringBuilder getCardTXT(int card, double totalPrice, double discount) {
        StringBuilder sb = new StringBuilder();
        if (card == CARD_RANGE_0) {
            sb.append(NO_CARD);
        } else {
            if (card > CARD_RANGE_0 && card < CARD_RANGE_100) {
                sb.append(CARD3 + card);
            }
            if (card >= CARD_RANGE_100 && card < CARD_RANGE_1000) {
                sb.append(CARD4 + card);
            }
            if (card >= CARD_RANGE_1000) {
                sb.append(CARD5 + card);
            }
        }
        sb.append(String.format(DISCOUNT, discount));
        sb.append(String.format(PRICE, totalPrice));
        return sb;
    }

    private StringBuilder getCardPDF(int card, double totalPrice, double discount) {
        StringBuilder sb = new StringBuilder();
        if (card == CARD_RANGE_0) {
            sb.append(INDENT + TRANSPORT + NO_CARD);
        } else {
            if (card > CARD_RANGE_0 && card < CARD_RANGE_100) {
                sb.append(INDENT + TRANSPORT + CARD3 + card);
            }
            if (card >= CARD_RANGE_100 && card < CARD_RANGE_1000) {
                sb.append(INDENT + TRANSPORT + CARD4 + card);
            }
            if (card >= CARD_RANGE_1000) {
                sb.append(INDENT + TRANSPORT + CARD5 + card);
            }
        }
        sb.append(String.format(DISCOUNT_PDF_FORMAT, DISCOUNT_PDF, discount));
        sb.append(String.format(PRICE_PDF_FORMAT, TOTAL_PRICE_PDF, totalPrice));
        return sb;
    }
}
