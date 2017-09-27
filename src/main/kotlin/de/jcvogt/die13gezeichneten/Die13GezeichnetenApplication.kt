package de.jcvogt.die13gezeichneten

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.annotation.SessionScope
import kotlin.reflect.full.memberProperties

data class Result(
        val wort: Int = 0,
        val verborgen: Int = 0,
        val naehr: Int = 0,
        val alchim: Int = 0,
        val blut: Int = 0,
        val stein: Int = 0,
        val gold: Int = 0,
        val glas: Int = 0,
        val holz: Int = 0,
        val rausch: Int = 0,
        val eisen: Int = 0,
        val gewoben: Int = 0,
        val ton: Int = 0
) {
    fun sign() = Result::class.memberProperties
            .associate { it -> Pair(it.name, it.get(this) as Int) }
            .maxBy { it -> it.value }?.key
}

class Step(val question: String, val options: Array<String>, val evaluator: (Int, Result) -> Result) {
}

@Component
@SessionScope
class ResultHolder {
    var result: Result = Result()
    var step: Int = 0

    fun evaluate(answer: Int, steps: Array<Step>): Boolean {
        if(step < steps.size) {
            result = steps[step].evaluator(answer, result)
            step += 1
        }
        return step >= steps.size
    }

    fun assess(): String {
        return mapOf(
                "wort" to "…Wortzeichen! Schau mal bei der Gilde der Dichter, Sänger, Schriftsteller, Buchdrucker vorbei!",
                "verborgen" to "…Verborgene Zeichen! Jemandem wie dir steht in Sygna keine Gilde offen! Pass auf deinen Lebenswandel auf, sonst landest du am Ende noch beim Verborgenen Hof!",
                "naehr" to "…Nährende Zeichen! Die Bäcker und Müller nehmen dich sicher gern auf! Oder aber die Heiler. Aber wir müssen dich vorwarnen: Letztere sind etwas umstritten!",
                "alchim" to "…Alchimistische Zeichen! Die Gilde der Alchimisten und Sterndeuter oder die Gilde der Heiler nehmen dich sicher gerne auf!",
                "blut" to "…Blutzeichen! Ein Schwertkämpfer! Die Goldene Gilde wäre etwas für dich. Oder du gehst zur Konkurrenz, den Stählernen Fechter, wenn dir ein Sitz im Rat nicht so wichtig ist.",
                "stein" to "…Steinerne Zeichen! Die Zunft der Steinmetze erwartet dich!",
                "gold" to "…Goldene Zeichen! Hier steht ein baldiger Schmuckschmiedegeselle vor uns!",
                "glas" to "…Gläserne Zeichen! Die Zunft der Glasbläser sucht immer gute Leute!",
                "holz" to "…Hölzerne Zeichen! Bewirb dich bei der erwürdigen Zunft der Schreiner und Zimmerleute!",
                "rausch" to "…Rausch! Die Gilde der Kurtisanen ist Vergangenheit, aber bei den Braumeistern kannst du es auch heute noch zu etwas bringen!",
                "eisen" to "…Eiserne Zeichen! Die Gilde der Klingenschmiede zum einen und die Gilde der Harnischermacher und Glockengießer zum anderen werden sich um dich reißen!",
                "gewoben" to "…Gewobene Zeichen! Strebe danache, die Meisterschaft in deiner Kunst in der Weberzunft zu erlangen!",
                "ton" to "... Irdene Zeichne! In der Zunft der Töpfer wirst du machtvolle Zeichen in den Ton brennen können!"
        ).get(result.sign())!!
    }

    fun reset(): Unit {
        result = Result()
        step = 0
    }

}

@SpringBootApplication
@Controller
class Die13GezeichnetenApplication(val resultHolder: ResultHolder) {
    val steps = arrayOf(
            Step("Womit arbeitest du lieber?",
                    arrayOf(
                            "Den Händen",
                            "Dem Kopf",
                            "Am liebsten gar nicht"
                    ),
                    { option: Int, r: Result ->
                        when (option) {
                            1 -> r.copy(
                                    naehr = r.naehr + 1,
                                    alchim = r.alchim + 1,
                                    blut = r.blut + 1,
                                    stein = r.stein + 1,
                                    gold = r.gold + 1,
                                    glas = r.glas + 1,
                                    holz = r.holz + 1,
                                    rausch = r.rausch + 1,
                                    eisen = r.eisen + 1,
                                    gewoben = r.gewoben + 1,
                                    ton = r.ton + 1
                            )
                            2 -> r.copy(wort = r.wort + 1)
                            3 -> r.copy(verborgen = r.verborgen + 1)
                            else -> r
                        }
                    }
            ),
            Step("Was ist das Highlight deines Tages?",
                    arrayOf(
                            "Ein gutes Buch lesen",
                            "Etwas Gutes essen",
                            "Mit Freunden einen trinken",
                            "Sport",
                            "Wenn ich dir das verraten würde, müsste ich dich töten."
                    ),
                    { option: Int, r: Result ->
                        when (option) {
                            1 -> r.copy(wort = r.wort + 1)
                            2 -> r.copy(naehr = r.naehr + 1)
                            3 -> r.copy(rausch = r.rausch + 1, alchim = r.alchim + 1)
                            4 -> r.copy(blut = r.blut + 1)
                            5 -> r.copy(verborgen = r.verborgen + 1)
                            else -> r
                        }
                    }
            ),
            Step("Deine Freunde schenken dir einen Workshop zum Geburtstag. Über welchen freust du dich?",
                    arrayOf(
                            "Schmieden!",
                            "Töpfern!",
                            "Schmuckbasteln!",
                            "Bierbrauen!",
                            "Nähen!",
                            "Ein Fechtseminar"
                    ),
                    { option: Int, r: Result ->
                        when (option) {
                            1 -> r.copy(eisen = r.eisen + 1)
                            2 -> r.copy(ton = r.ton + 1)
                            3 -> r.copy(gold = r.gold + 1)
                            4 -> r.copy(rausch = r.rausch + 1)
                            5 -> r.copy(gewoben = r.gewoben + 1)
                            6 -> r.copy(blut = r.blut + 1)
                            else -> r
                        }
                    }
            ),
            Step("Wofür würdest du in deinem Haushalt wirklich Geld in die Hand nehmen?",
                    arrayOf(
                            "Ein tolles japanisches Schneidemesser",
                            "Das passende Glas für alle Gelegenheiten",
                            "Neue Möbel",
                            "Haushalskram? Pah! Ich brauche Klamotten!",
                            "Ich brauche vor allen Dingen Bücher!",
                            "Einen guten Whiskey",
                            "Ich bin ein Bastler, ich baue immer an meinem Zuhause herum."
                    ),
                    { option: Int, r: Result ->
                        when (option) {
                            1 -> r.copy(eisen = r.eisen + 1)
                            2 -> r.copy(glas = r.glas + 1)
                            3 -> r.copy(holz = r.holz + 1)
                            4 -> r.copy(gewoben = r.gewoben + 1)
                            5 -> r.copy(wort = r.wort + 1)
                            6 -> r.copy(rausch = r.rausch + 1)
                            7 -> r.copy(stein = r.stein + 1, glas = r.glas + 1)
                            else -> r
                        }
                    }
            ),
            Step("Du machst Urlaub. Wo übernachtest du?",
                    arrayOf(
                            "In einem Zelt",
                            "In einem Hotel",
                            "In einer Blockhütte",
                            "Ich nehm’s wie’s kommt, notfalls penn ich im Auto."
                    ),
                    { option: Int, r: Result ->
                        when (option) {
                            1 -> r.copy(gewoben = r.gewoben + 1)
                            2 -> r.copy(stein = r.stein + 1)
                            3 -> r.copy(holz = r.holz + 1)
                            4 -> r.copy(verborgen = r.verborgen + 1)
                            else -> r
                        }
                    }
            ),
            Step("Du arbeitest in einem Labor und hast die Wahl:",
                    arrayOf(
                            "Ich suche nach dem Unsterblichkeitselixier.",
                            "Ich forsche nach Nährlösung, die den Hunger besiegt.",
                            "Ich möchte einen Trank, der unsichtbar macht.",
                            "Ich braue einen Liebeszauber.",
                            "Ich möchte Blei zu Gold machen."
                    ),
                    { option: Int, r: Result ->
                        when (option) {
                            1 -> r.copy(alchim = r.alchim + 1, blut = r.blut + 1)
                            2 -> r.copy(naehr = r.naehr + 1)
                            3 -> r.copy(verborgen = r.verborgen + 1)
                            4 -> r.copy(rausch = r.rausch + 1)
                            5 -> r.copy(gold = r.gold + 1)
                            else -> r
                        }
                    }
            ),
            Step("Was ist mächtiger?",
                    arrayOf(
                            "Die Feder",
                            "Das Schwert",
                            "Das kannst du beides vergessen, im Notfall hilft nur Verschlagenheit."
                    ),
                    { option: Int, r: Result ->
                        when (option) {
                            1 -> r.copy(wort = r.wort + 1)
                            2 -> r.copy(blut = r.blut + 1, eisen = r.eisen + 1)
                            3 -> r.copy(verborgen = r.verborgen + 1)
                            else -> r
                        }
                    }
            ),
            Step("Einbrecher entwenden, worin dein Herzblut steckt. Was ist es?",
                    arrayOf(
                            "Sie haben meinen Nutzgarten verwüstet!",
                            "Sie haben mein Porzellan zertrümmert!",
                            "Sie haben meinen Schmuck gestohlen!",
                            "Sie haben meinen Laptop geklaut!",
                            "Sie haben meine Fensterscheiben eingeschlagen!"
                    ),
                    { option: Int, r: Result ->
                        when (option) {
                            1 -> r.copy(naehr = r.naehr + 1)
                            2 -> r.copy(ton = r.ton + 1)
                            3 -> r.copy(gold = r.gold + 1)
                            4 -> r.copy(wort = r.wort + 1)
                            5 -> r.copy(glas = r.glas + 1)
                            else -> r
                        }
                    }
            ),
            Step("Die Weihnachtsbäume sind aus. Du hast deiner Familie versprochen, dass du einen mitbringst. Was tust du?",
                    arrayOf(
                            "Ich nehme einen künstlichen und behänge ihn mit so viel Kugeln und Bling-Bling, dass sie es nicht merken.",
                            "Ich war immer schon gut mit der Laubsäge – das sieht nachher sicher nett aus.",
                            "Das gute Essen wird meine Familie drüber hinwegtrösten. Ich kaufe extra opulent ein.",
                            "Stattdessen kaufe ich einfach zu Silvester mehr Feuerwerk.",
                            "Ich stelle einfach einen Haufen Kerzenleuchter auf."
                    ),
                    { option: Int, r: Result ->
                        when (option) {
                            1 -> r.copy(gold = r.gold + 1, glas = r.glas + 1)
                            2 -> r.copy(holz = r.holz + 1)
                            3 -> r.copy(naehr = r.naehr + 1)
                            4 -> r.copy(alchim = r.alchim + 1)
                            5 -> r.copy(eisen = r.eisen + 1)
                            else -> r
                        }
                    }
            ),
            Step("Alltagskram, bei dem es deiner Meinung nach Verbesserungspotenzial gibt:",
                    arrayOf(
                            "Geschirr, das nicht zerbricht",
                            "Kleidung, die nicht zerreißt",
                            "Straßen, die keine Schlaglöcher kriegen",
                            "Zahnpasta, von der ich WIRKLICH keine Parodontose kriege"
                    ),
                    { option: Int, r: Result ->
                        when (option) {
                            1 -> r.copy(ton = r.ton + 1)
                            2 -> r.copy(gewoben = r.gewoben + 1)
                            3 -> r.copy(stein = r.stein + 1)
                            4 -> r.copy(alchim = r.alchim + 1)
                            else -> r
                        }
                    }
            ),
            Step("Wie willst du beerdigt werden?",
                    arrayOf(
                            "Ich will den Klassiker: Gebt mir einen Sarg!",
                            "Ich möchte zu Erde werden – äschert mich ein und verstreut mich.",
                            "Wie Schneewittchen und Lenin: in einem Glassarg!",
                            "Ich hätte gern eine Pyramide."
                    ),
                    { option: Int, r: Result ->
                        when (option) {
                            1 -> r.copy(holz = r.holz + 1)
                            2 -> r.copy(ton = r.ton + 1)
                            3 -> r.copy(glas = r.glas + 1)
                            4 -> r.copy(stein = r.stein + 1)
                            else -> r
                        }
                    }
            )
    )

    @GetMapping("/")
    fun index(): String {
        reset()
        return "index"
    }

    @GetMapping("/gildenrat")
    fun gildenrat(model: Model): String {
        model
                .addAttribute("questionNumber", resultHolder.step + 1)
                .addAttribute("step", steps[resultHolder.step])
        return "question"
    }

    @PostMapping("/answer")
    fun answer(@RequestParam(required = false) answer: Int?) =
            if(answer != null && resultHolder.evaluate(answer, steps)) "redirect:/result" else "redirect:/gildenrat"

    @GetMapping("/result")
    fun result(model: Model): String {
        model.addAttribute("result", resultHolder.assess())
        return "result"
    }

    @PostMapping("/reset")
    fun reset(): String {
        resultHolder.reset()
        return "redirect:/gildenrat"
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Die13GezeichnetenApplication::class.java, *args)
}
