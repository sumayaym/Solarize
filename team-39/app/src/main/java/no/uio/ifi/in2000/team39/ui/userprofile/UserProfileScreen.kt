package no.uio.ifi.in2000.team39.ui.userprofile


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import no.uio.ifi.in2000.team39.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn


@Composable
fun UserProfileScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { TopBar() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { UserProfileCard() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { AddressCard() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { InsightsCard() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { ProductionAndSavingsCards() }
    }
}


@Composable
fun TopBar() {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            . background(Color(0xFF1E3A8A))
            .padding(16.dp),
        contentAlignment = Alignment.Center

    ){
        Text("Watt Wise", color = Color.White, fontSize= 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}


@Composable
fun  UserProfileCard() { // Bruker profil
    Card (
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ){
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface ( // "profil bilde" kan godt fjernes, er bare for at det skal se finere ut nå
                        modifier = Modifier.size (50.dp),
                        shape = CircleShape,
                        color = Color (0xFFE0E7FF)
                    ){
                        Box (contentAlignment = Alignment.Center) {
                            Text("LA", color = Color (0xFF4F46E5))
                        }
                    }

                }

                Spacer(modifier = Modifier.width(12.dp))
                Column { // hardkodet profil for nå
                    Text ("Laiba Ansar", fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(24.dp))

                // dropdown meny for å bytte bolig
                HomeSelectionDropdown(
                    homes = listOf("Gaustadalléen 23B", "Storgata 45", "Bjørnveien 12"),
                    selectedHome = "Gaudstadallèen 23B",
                    onHomeSelected = { } // bolig må bli endret her
                )
            }


        }
    }


}


@Composable
fun AddressCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)) // Lys grå bakgrunn
    ) {

        Row (
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        Image (
            painter = painterResource(id = R.drawable.solarhouse),
            contentDescription = "Hus bilde",
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp)) // Runde hjørner
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Gaustadalléen 23B, Oslo",
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("1600 m² tak areal", fontSize = 14.sp, color = Color.Gray)
            Text("15.2 kWh/dag gjennomsnitt", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                // harkodet nå, men skal vise hvor mye av ditt totale strømforburk som er dekket av solenergi FOR EKSMEPEL!
                progress = { 0.66f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = Color.Green,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("66%", fontSize = 14.sp, color = Color(0xFF059669) // prosenten skal også endres, altså avhenign av boli
            )
        }
            }
    }
}
@Composable
fun  InsightsCard() {
    Card (
        shape = RoundedCornerShape(12.dp),
        modifier= Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color( 0xFFF0FDF4))
    ) {
        Column (modifier = Modifier.padding(16.dp)) { // displaye værdata fra Frosts API
            Text ("Været nå",fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF047857) )
            Text ("🌞 Sol • 18°C                           + 38%" ,fontSize = 16.sp, color = Color(0xFF059669),fontWeight = androidx.compose.ui.text.font.FontWeight.Bold )
        // senere bruke enum klasser bytte melom sol, regn og snø emoji eller bilde basert på API'en

        }
    }
}

@Composable
fun ProductionAndSavingsCards(){ // hvor mye sol prodosert + hvor mye bruker har spart

    Row (modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Card (
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column (modifier = Modifier.padding(16.dp)) {
                Text("Produksjon", fontSize = 18.sp,fontWeight =  androidx.compose.ui.text.font.FontWeight.Bold )
                Spacer(modifier = Modifier.height(60.dp)) // her kan det ligge en faktisk graf. dette er bare plassholder nå
            }
        }

        Card (
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💰 Spart hittil", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text ("12 000 kr spart", fontSize = 16.sp, fontWeight =  androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }
    }
}





@Composable
fun HomeSelectionDropdown( // en knapp med dropdown menu for å bytte bolig
    homes: List<String>,
    selectedHome: String,
    onHomeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0)),
            modifier = Modifier
                .height(42.dp)
                .width(150.dp)
        ) {
            Text("Bytt bolig", fontSize = 12.sp, color = Color.Black)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            homes.forEach { home ->
                DropdownMenuItem(
                    text = { Text(home) },
                    onClick = {
                        onHomeSelected(home)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfilePreview () {
    UserProfileScreen()
}
