/**
 * 
 */

paypal.Button.render({

	env : 'sandbox', // Or 'sandbox',

	commit : true, // Show a 'Pay Now' button
	
	client: {
        sandbox:    'AevwEdMZ08RgSWXP2eNkGnBxFxuAEL8PGQEkdwLL8Dy-XNUOua4Ui0RwdABuq7J-Cmwm63XYNvrrp-aI',
        production: 'xxxxxxxxx'
    },
    
    style: {
        label: 'checkout', // checkout || credit
        size:  'small',    // tiny | small | medium
        shape: 'pill',     // pill | rect
        color: 'blue'      // gold | blue | silver
    },

	payment : function(data, actions) {
		var env    = this.props.env;
        var client = this.props.client;
        var total = document.getElementById("total").textContent;
        console.log("Total amount is: "+total);

        return paypal.rest.payment.create(env, client, {
            transactions: [
                {
                    amount: { total: total, currency: 'USD' }
                }
            ]
        });
    },

	onAuthorize : function(data, actions) {
		// Execute the payment here
		return actions.payment.execute().then(function(payment) {

            // The payment is complete!
            // You can now show a confirmation message to the customer
			document.getElementById("payment-success").style.display="inline";
        });
	}

}, '#paypal-button');
