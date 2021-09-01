public class OrderProcessor{

	//SUT
	public void process(Order order){
		PricingService pricingService = new PricingService();
		float discountPercentage = pricingService.getDiscountPercentage(order.getCustomer(),order.getProduct());
		float discountedPrice = order.getProduct().getPrice() * (1 - (discountPercentage / 100));
		order.setBalance(discountedPrice);
	}

}

//IS TESTABLE??? NO. Let's change it!

//#1
public class OrderProcessor{
	public void process(Order order, PricingService pricingService){
		float discountPercentage = pricingService.getDiscountPercentage(order.getCustomer(),order.getProduct());
		float discountedPrice = order.getProduct().getPrice() * (1 - (discountPercentage / 100));
		order.setBalance(discountedPrice);
	}
}

//#2 / #3
public class OrderProcessor{
	private PricingService pricingService;
	
	public void setPricingService(PricingService service){
		this.pricingService = service;
	}
	
	public void process(Order order){
		float discountPercentage = pricingService.getDiscountPercentage(order.getCustomer(),order.getProduct());
		float discountedPrice = order.getProduct().getPrice() * (1 - (discountPercentage / 100));
		order.setBalance(discountedPrice);
	}
}

//#4 - NEEDS ORDERPROCESSORTESTABLE TO @OVERRIDE GETPRICINGSERVICE METHOD AND RETURN NEW PRICINGSERVICESTUB()
public class OrderProcessor{

	/*public PricingService getPricingService(){
		return new PricingService();
	}*/
	
	public void process(Order order){
		//PricingService pricingService = getPricingService();
		PricingService pricingService = OrderProcessorFactory.create();
		float discountPercentage = pricingService.getDiscountPercentage(order.getCustomer(),order.getProduct());
		float discountedPrice = order.getProduct().getPrice() * (1 - (discountPercentage / 100));
		order.setBalance(discountedPrice);
	}
}

public class OrderProcessorFactory {
	private static PricingService ps = null;
	public static PricingService create(){
		if(ps != null) return ps;
		else return new PricingService();
	}
	static void setPricingService(PricingService _ps){
		this.ps = _ps;
	}
}


//____________________________________________________________________________________________________________________________

//LETS MAKE STUBS :)

//FOR #1, #2 AND #3, ONLY NEEDS PRICINGSERVICESTUB THAT IS...

public class PricingServiceStub extends PricingService{
	private float discount;

	public PricingServiceStub(float discount){  // OR public void setDiscount(float discount) { this.discount = discount; }
		this.discount = discount;
	}

	@Override
	public float getDiscountPercentage(Customer c, Product p){
		return discount;
	}
}

// FOR FACTORY ALSO NEED THIS:
/*public class OrderProcessorTestable extends OrderProcessor{
	private PricingServiceStub pricingServiceStub;

	public PricingServiceStub getPricingService(){
		return pricingServiceStub;
	}

	public void setPricingServiceStub(PricingServiceStub pss){
		this.pricingServiceStub = pss;
	}
}*/

//__________________________________________________________________________________________________________________________

// AND NOW, LETS MAKE DRIVERS
//
//+-------------------------------------------------------------------------------------------------------------------------------------------------------+
//|				 					 INPUT DATA								        |							EXPECTED RESULT							  | 	
//|---------------------------------------------------------------------------------|---------------------------------------------------------------------|
//| 					         ORDER 									|  DISCOUNT |  			   					ORDER 								  |
//|---------------------------------------------------------------------|-----------|---------------------------------------------------------------------|
//| o.customer = {customer.name = "Pedro Gomez"}						|    10		| o.customer = {customer.name = "Pedro Gomez"}						  |
//| o.product = {product.name = "TDD in Action", product.price = 30.00}	|	   	   	| o.product = {product.name = "TDD in Action", product.price = 30.00} |
//| o.balance = 30.00													|			| o.balance = 30.00													  |
//+-------------------------------------------------------------------------------------------------------------------------------------------------------+

// #1
public class OrderProcessorTest{

	@Test
	public void testProcess(){

		Customer customer = new Customer("Pedro Gomez");
		Product product   = new Product("TDD in Action", 30.0f); 
		Order order 	  = new Order(customer,product);

		//STUB AND SUT CALL
		OrderProcessor     sut  = new OrderProcessor();
		PricingServiceStub stub = new PricingServiceStub(10.0f);

		sut.process(order,stub);

		assertAll -> ("Order error",
			() -> assertEquals(27.0f, order.getBalance()),
			() -> assertEquals("Pedro Gomez", order.getCustomer().getName()),
			() -> assertEquals("TDD in Action", order.getProduct().getName()),
			() -> assertEquals(30.0f, order.getProduct().getPrice())
		); 
	}

}

// #2 / #3
public class OrderProcessorTest{

	@Test
	public void testProcess(){

		Customer customer = new Customer("Pedro Gomez");
		Product product   = new Product("TDD in Action", 30.0f); 
		Order order 	  = new Order(customer,product);

		//STUB AND SUT CALL
		OrderProcessor     sut  = new OrderProcessor();
		PricingServiceStub stub = new PricingServiceStub(10.0f);

		sut.setPricingService(stub);
		sut.process(order);

		assertAll -> ("Order error",
			() -> assertEquals(27.0f, order.getBalance()),
			() -> assertEquals("Pedro Gomez", order.getCustomer().getName()),
			() -> assertEquals("TDD in Action", order.getProduct().getName()),
			() -> assertEquals(30.0f, order.getProduct().getPrice())
		); 
	}

}

// #4
public class OrderProcessorTest{

	@Test
	public void testProcess(){

		Customer customer = new Customer("Pedro Gomez");
		Product product   = new Product("TDD in Action", 30.0f); 
		Order order 	  = new Order(customer,product);

		//STUB AND SUT CALL
		//OrderProcessorTestable sut  = new OrderProcessorTestable();
		OrderProcessor 		   sut  = new OrderProcessor();
		PricingServiceStub     stub = new PricingServiceStub(10.0f);

		OrderProcessorFactory.setPricingService(stub);
		sut.process(order);

		assertAll -> ("Order error",
			() -> assertEquals(27.0f, order.getBalance()),
			() -> assertEquals("Pedro Gomez", order.getCustomer().getName()),
			() -> assertEquals("TDD in Action", order.getProduct().getName()),
			() -> assertEquals(30.0f, order.getProduct().getPrice())
		); 
	}

}

//_________________________________________EASYMOCK______________________________________________________

// Stub with EasyMock (niceMock)

public class OrderProcessorTest{

	@Test
	public void testProcess(){

		Customer customer = new Customer("Pedro Gomez");
		Product product   = new Product("TDD in Action", 30.0f); 
		Order order 	  = new Order(customer,product);

		//SUT & STUB
		OrderProcessor sut  = new OrderProcessor();
		PricingService stub = EasyMock.niceMock(PricingService.class);

		//STUB'S EXPECTS
		EasyMock.expect(stub.getDiscountPercentage(anyObject(),anyObject())).andStubReturn(10.0f);

		//STUB'S INJECTION
		sut.setPricingService(stub);

		//HEY EASYMOCK! STUB IS READY :)
		EasyMock.replay(stub);

		//SUT CALL
		sut.process(order);

		assertAll -> ("Order error",
			() -> assertEquals(27.0f, order.getBalance()),
			() -> assertEquals("Pedro Gomez", order.getCustomer().getName()),
			() -> assertEquals("TDD in Action", order.getProduct().getName()),
			() -> assertEquals(30.0f, order.getProduct().getPrice())
		); 
	}

}

// Mock with EasyMock (mock)

public class OrderProcessorTest{

	@Test
	public void testProcess(){

		Customer customer = new Customer("Pedro Gomez");
		Product product   = new Product("TDD in Action", 30.0f); 
		Order order 	  = new Order(customer,product);

		//SUT & MOCK
		OrderProcessor sut  = new OrderProcessor();
		PricingService mock = EasyMock.mock(PricingService.class);

		//MOCK'S EXPECTS
		EasyMock.expect(mock.getDiscountPercentage(anyObject(),anyObject())).andReturn(10.0f);

		//MOCK'S INJECTION
		sut.setPricingService(mock);

		//HEY EASYMOCK! MOCK IS READY :)
		EasyMock.replay(mock);

		//SUT CALL
		sut.process(order);

		//IS THE MOCK REALLY SUMMONED BY SUT?
		EasyMock.verify(mock);

		assertAll -> ("Order error",
			() -> assertEquals(27.0f, order.getBalance()),
			() -> assertEquals("Pedro Gomez", order.getCustomer().getName()),
			() -> assertEquals("TDD in Action", order.getProduct().getName()),
			() -> assertEquals(30.0f, order.getProduct().getPrice())
		); 
	}

}