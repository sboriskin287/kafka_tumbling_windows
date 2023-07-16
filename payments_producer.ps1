for ($i = 0;;$i++) {
$price = Get-Random -Minimum 1 -Maximum 1000
$jsonObject = @{
    "price" = $price
    "messageCount" = $i
} | ConvertTo-Json -Compress
Write-Output $jsonObject | .\windows\kafka-console-producer.bat --bootstrap-server localhost:19092,localhost:29092,localhost:39092 --topic payments
Write-Output $jsonObject
Start-Sleep -Milliseconds 200
}
