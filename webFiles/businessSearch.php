<html>
<head>
<style>
table{

}
td{
border:1px solid black;
padding:5px;
}
.description{
position:relative;
width:400px;
}
.title{
width:300px;
}
</style>
</head>
<body>
<form method="GET">
Search Title: <input type="text" name="title" value="<?php if(!empty($_GET['title'])){print $_GET['title'];} ?>"></br>
Search Description: <input type="text" name="description" value="<?php if(!empty($_GET['description'])){print $_GET['description'];} ?>"></br>
Revenue: <input type="text" name="revenuelow" value="<?php if(!empty($_GET['revenuelow'])){print $_GET['revenuelow'];} ?>" > to <input type="text" name="revenuehigh" value="<?php if(!empty($_GET['revenuehigh'])){print $_GET['revenuehigh'];} ?>"><br>
Cashflow: <input type="text" name="cashflowlow" value="<?php if(!empty($_GET['cashflowlow'])){print $_GET['cashflowlow'];} ?>" > to <input type="text" name="cashflowhigh" value="<?php if(!empty($_GET['cashflowhigh'])){print $_GET['cashflowhigh'];} ?>"><br>
Asking Price: <input type="text" name="pricelow" value="<?php if(!empty($_GET['pricelow'])){print $_GET['pricelow'];} ?>"> to <input type="text" name="pricehigh" value="<?php if(!empty($_GET['pricehigh'])){print $_GET['pricehigh'];} ?>"><br>
Number of Results: <input type="text" name="results" value="<?php if(!empty($_GET['results'])){print $_GET['results'];} ?>">
<input type="submit" value="submit">
</form>
<?php
//index address city state zip county country region price revenue cashflow ebitda realestatevalue realestateincluded 
//ffevalue ffeincluded inventory inventoryincluded industry description sellerfinancingavailable squarefeet numberemployees
//bcompany bname bphone baddress bwebsite url linfo dateposted dateremoved lid timestamp

// Create connection
$con=mysqli_connect("localhost","root","","brookwood");

// Check connection
if (mysqli_connect_errno($con))
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }
?>

<?php

if(empty($_GET['results'])){
$_GET['results'] = 10;
}
$restrictions = "";

if(!empty($_GET['title'])){
	$restrictions = $restrictions." AND title LIKE '%".$_GET['title']."%'";
}
if(!empty($_GET['description'])){
$restrictions = $restrictions." AND description LIKE '%".$_GET['description']."%'";
}

if(!empty($_GET['revenuelow']) and is_numeric($_GET['revenuelow'])){
	$restrictions = $restrictions." AND revenue >= ".$_GET['revenuelow'];
}

if(!empty($_GET['revenuehigh']) and is_numeric($_GET['revenuehigh'])){
	$restrictions = $restrictions." AND revenue <= ".$_GET['revenuehigh']."";
}

if(!empty($_GET['pricelow']) and is_numeric($_GET['pricelow'])){
	$restrictions = $restrictions." AND price >= ".$_GET['pricelow']."";
}
if(!empty($_GET['pricehigh']) and is_numeric($_GET['pricehigh'])){
	$restrictions = $restrictions." AND price <= ".$_GET['pricehigh']."";
}
if(!empty($_GET['cashflowlow']) and is_numeric($_GET['cashflowlow'])){
	$restrictions = $restrictions." AND cashflow >= ".$_GET['cashflowlow']."";
}
if(!empty($_GET['cashflowhigh']) and is_numeric($_GET['cashflowhigh'])){
	$restrictions = $restrictions." AND cashflow <= ".$_GET['cashflowhigh']."";
}
//Remove the first instance of "AND" to make this work
$restrictions = substr($restrictions, 4);

//$result = mysqli_query($con, "SELECT * FROM data WHERE  SELECT * FROM `data` WHERE title like '%Birmingham%' LIMIT ".$_GET['results']);
if($restrictions == ""){
	$result = mysqli_query($con, "SELECT * FROM `data` WHERE 1 LIMIT ".$_GET['results']);
	print "SELECT * FROM `data` WHERE 1 LIMIT ".$_GET['results'];
}else{
	$result = mysqli_query($con, "SELECT * FROM `data` WHERE ".$restrictions." LIMIT ".$_GET['results']);
	print "SELECT * FROM `data` WHERE ".$restrictions." LIMIT ".$_GET['results'];
}

//print "Result is: ".$result;
print "<table>";

while ($row = mysqli_fetch_assoc($result)) {
	print "<tr><td class='title'>";
	print "<a href='".$row['url']."' target='_blank'>".$row['title']."</a><p>";
	print "State: ".$row['state']."<br>";
	print "Revenue: ".$row['revenue']."<br>";
	print "Price: ".$row['price']."<br>";
	print "Cashflow: ".$row['cashflow']."<br>";
	print "</td><td class='description'>";
	print substr($row['description'], 0, 1000);
	print "</td></tr>";
}
print "</table>";
?>
</body>