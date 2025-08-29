# Printer module for Titanium (Android)

## Requirements

- Titanium SDK 12.7.0

## Example
```js
const win = Ti.UI.createWindow({layout:"vertical"});
const btn1 = Ti.UI.createButton({title:"print image"});
const btn2 = Ti.UI.createButton({title:"print pdf"});
const btn3 = Ti.UI.createButton({title:"print html"});
const img = Ti.UI.createImageView({image:"/appicon.png"});

win.add([img, btn1,btn2,btn3]);
win.open();

const print = require("ti.print");

btn1.addEventListener("click", function() {
	// print image
	print.printImage({
		jobName: "image",
		image: img.toBlob()
	});
});

btn2.addEventListener("click", function() {
	// print pdf from app/assets/1.pdf
	print.printFile({
		jobName: "pdf",
		url: "/1.pdf"
	});
});

btn3.addEventListener("click", function() {
	// print html
	var localHTML = "<h1>external image</h1><img src='https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png'/><br/><h2>local image</h2><img src='appicon.png'/>";
	print.printHTML({
		jobName: "test",
		html: localHTML
	});
});
```

## Author

- Michael Gangolf ([@MichaelGangolf](https://twitter.com/MichaelGangolf) / [Web](http://migaweb.de)) <span class="badge-buymeacoffee"><a href="https://www.buymeacoffee.com/miga" title="donate"><img src="https://img.shields.io/badge/buy%20me%20a%20coke-donate-orange.svg" alt="Buy Me A Coke donate button" /></a></span>
