package com.email.modifications;

//import com.codingerror.model.AddressDetails;

import com.itextpdf.kernel.color.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddressDetails {

	private String billingInfoText = Constants.BILLING_INFO;
	private String shippingInfoText = Constants.SHIPPING_INFO;
	private String billingCompanyText = Constants.BILLING_COMPANY;
	private String billingCompany = Constants.EMPTY;
//	private String billingNameText = Constants.BILLING_NAME;
	private String billingName = Constants.EMPTY;
	private String billingAddressText = Constants.BILLING_ADDRESS;
	private String billingAddress = Constants.EMPTY;
	private String billingEmailText = Constants.BILLING_EMAIL;
	private String billingEmail = Constants.EMPTY;

	private String shippingNameText = Constants.SHIPPING_NAME;
	private String shippingName = Constants.EMPTY;
//	private String shippingAddressText = Constants.SHIPPING_ADDRESS;
//	private String shippingAddress = Constants.EMPTY;
	private Color borderColor = Color.GRAY;

	public AddressDetails setBillingInfoText(String billingInfoText) {
		this.billingInfoText = billingInfoText;
		return this;
	}

	public AddressDetails setShippingInfoText(String shippingInfoText) {
		this.shippingInfoText = shippingInfoText;
		return this;
	}

	public AddressDetails setBillingCompanyText(String billingCompanyText) {
		this.billingCompanyText = billingCompanyText;
		return this;
	}

	public AddressDetails setBillingCompany(String billingCompany) {
		this.billingCompany = billingCompany;
		return this;
	}

//	public AddressDetails setBillingNameText(String billingNameText) {
//		this.billingNameText = billingNameText;
//		return this;
//	}

	public AddressDetails setBillingName(String billingName) {
		this.billingName = billingName;
		return this;
	}

	public AddressDetails setBillingAddressText(String billingAddressText) {
		this.billingAddressText = billingAddressText;
		return this;
	}

	public AddressDetails setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
		return this;
	}

	public AddressDetails setBillingEmailText(String billingEmailText) {
		this.billingEmailText = billingEmailText;
		return this;
	}

	public AddressDetails setBillingEmail(String billingEmail) {
		this.billingEmail = billingEmail;
		return this;
	}

	public AddressDetails setShippingNameText(String shippingNameText) {
		this.shippingNameText = shippingNameText;
		return this;
	}

	public AddressDetails setShippingName(String shippingName) {
		this.shippingName = shippingName;
		return this;
	}

//	public AddressDetails setShippingAddressText(String shippingAddressText) {
//		this.shippingAddressText = shippingAddressText;
//		return this;
//	}
//
//	public AddressDetails setShippingAddress(String shippingAddress) {
//		this.shippingAddress = shippingAddress;
//		return this;
//	}

	public AddressDetails setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}

	public AddressDetails build() {
		return this;
	}

}
