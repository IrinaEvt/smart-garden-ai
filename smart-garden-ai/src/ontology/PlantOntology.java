package ontology;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class PlantOntology {
    private OWLOntologyManager ontoManager;
    private OWLOntology plantOntology;
    private OWLDataFactory dataFactory;
    private String ontologyIRIStr;

    public PlantOntology(){
        ontoManager = OWLManager.createOWLOntologyManager();
        dataFactory = ontoManager.getOWLDataFactory();
        loadOntology();

        ontologyIRIStr = plantOntology.getOntologyID().getOntologyIRI().toString() + "#";
    }

    private void loadOntology() {
        File ontology = new File("resources/plant_ontology.owl");

        try {
            plantOntology = ontoManager.loadOntologyFromOntologyDocument(ontology);
        } catch (OWLOntologyCreationException e) {
            System.out.println(e.getMessage());
        }
    }

    private String getClassFriendlyName(OWLClassExpression expr) {
        if (!expr.isAnonymous()) {
            String iri = expr.asOWLClass().getIRI().toString();
            return iri.substring(iri.indexOf("#") + 1);
        }
        return expr.toString();
    }

    private String getIndividualFriendlyName(IRI iri) {
        String iriStr = iri.toString();
        return iriStr.substring(iriStr.indexOf("#") + 1);
    }




    public void createPlantIndividual(models.Plant plant) {
        // 1. Създаване на индивид на растението
        OWLNamedIndividual plantIndiv = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + plant.getName()));

        // 2. Деклариране на клас (тип) на растението
        OWLClass plantType = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + plant.getType()));
        if (!plantOntology.containsClassInSignature(plantType.getIRI())) {
            // Ако класът не съществува – го декларираме
            OWLDeclarationAxiom declareClass = dataFactory.getOWLDeclarationAxiom(plantType);
            ontoManager.applyChange(new AddAxiom(plantOntology, declareClass));
        }

        OWLClassAssertionAxiom axPlant = dataFactory.getOWLClassAssertionAxiom(plantType, plantIndiv);
        ontoManager.applyChange(new AddAxiom(plantOntology, axPlant));


        if (plant.getSymptoms() != null) {
            for (String symptom : plant.getSymptoms()) {
                String symptomIndivName = plant.getName() + "_" + symptom;
                OWLNamedIndividual symptomIndiv = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + symptomIndivName));
                OWLClass symptomClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + symptom));
                OWLClassAssertionAxiom classAssertion = dataFactory.getOWLClassAssertionAxiom(symptomClass, symptomIndiv);
                ontoManager.applyChange(new AddAxiom(plantOntology, classAssertion));

                OWLObjectProperty hasSymptom = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasSymptom"));
                OWLObjectPropertyAssertionAxiom link = dataFactory.getOWLObjectPropertyAssertionAxiom(hasSymptom, plantIndiv, symptomIndiv);
                ontoManager.applyChange(new AddAxiom(plantOntology, link));
            }
        }

      //  saveOntology();
    }


    public void addSymptomToPlantIndividual(String plantIndivName, String symptomClassName, String symptomIndivName) {
        OWLNamedIndividual plantIndiv = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + plantIndivName));
        OWLNamedIndividual symptomIndiv = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + symptomIndivName));
        OWLClass symptomClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + symptomClassName));

        // Само създаване на индивид от клас, без декларация за самия клас
        OWLClassAssertionAxiom classAssertion = dataFactory.getOWLClassAssertionAxiom(symptomClass, symptomIndiv);
        ontoManager.applyChange(new AddAxiom(plantOntology, classAssertion));

        // Свързване със симптома
        OWLObjectProperty hasSymptom = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasSymptom"));
        OWLObjectPropertyAssertionAxiom link = dataFactory.getOWLObjectPropertyAssertionAxiom(hasSymptom, plantIndiv, symptomIndiv);
        ontoManager.applyChange(new AddAxiom(plantOntology, link));

        saveOntology();
    }


    public List<String> getAdviceForPlantIndividual(String plantIndivName) {
        List<String> result = new ArrayList<>();
        OWLNamedIndividual plantIndiv = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + plantIndivName));
        OWLObjectProperty hasSymptom = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasSymptom"));

        for (OWLObjectPropertyAssertionAxiom ax : plantOntology.getObjectPropertyAssertionAxioms(plantIndiv)) {
            if (ax.getProperty().asOWLObjectProperty().equals(hasSymptom)) {
                OWLNamedIndividual symptomIndiv = ax.getObject().asOWLNamedIndividual();
                String symptomName = getIndividualFriendlyName(symptomIndiv.getIRI());
                result.add("Симптом: " + symptomName);

                for (OWLClassExpression type : symptomIndiv.getTypes(plantOntology)) {
                    if (!type.isAnonymous()) {
                        OWLClass symptomClass = type.asOWLClass();

                        for (OWLSubClassOfAxiom sa : plantOntology.getSubClassAxiomsForSubClass(symptomClass)) {
                            if (sa.getSuperClass() instanceof OWLObjectSomeValuesFrom) {
                                OWLObjectSomeValuesFrom restriction = (OWLObjectSomeValuesFrom) sa.getSuperClass();
                                String propName = getClassFriendlyName(dataFactory.getOWLClass(restriction.getProperty().asOWLObjectProperty().getIRI()));
                                OWLClassExpression causeExpr = restriction.getFiller();

                                if (!causeExpr.isAnonymous()) {
                                    OWLClass causeClass = causeExpr.asOWLClass();
                                    String causeName = getClassFriendlyName(causeClass);
                                    if (propName.toLowerCase().contains("cause")) {
                                        result.add("Възможна причина: " + causeName);
                                    }

                                    for (OWLSubClassOfAxiom ca : plantOntology.getSubClassAxiomsForSubClass(causeClass)) {
                                        if (ca.getSuperClass() instanceof OWLObjectSomeValuesFrom) {
                                            OWLObjectSomeValuesFrom actionRestriction = (OWLObjectSomeValuesFrom) ca.getSuperClass();
                                            String actionProp = getClassFriendlyName(dataFactory.getOWLClass(actionRestriction.getProperty().asOWLObjectProperty().getIRI()));
                                            OWLClassExpression actionExpr = actionRestriction.getFiller();

                                            if (!actionExpr.isAnonymous()) {
                                                String actionClassName = getClassFriendlyName(actionExpr.asOWLClass());
                                                boolean foundActionIndividual = false;

                                                for (OWLNamedIndividual actionIndiv : plantOntology.getIndividualsInSignature()) {
                                                    for (OWLClassExpression actionType : actionIndiv.getTypes(plantOntology)) {
                                                        if (!actionType.isAnonymous() && actionType.asOWLClass().equals(actionExpr.asOWLClass())) {
                                                            String actionName = getIndividualFriendlyName(actionIndiv.getIRI());
                                                            if (actionProp.toLowerCase().contains("care") || actionProp.toLowerCase().contains("treat")) {
                                                                result.add("Препоръчано действие: " + actionName);
                                                                foundActionIndividual = true;
                                                            }
                                                        }
                                                    }
                                                }

                                                if (!foundActionIndividual) {
                                                    result.add("Препоръчано действие (клас): " + actionClassName);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public void removePlantAndSymptoms(String plantName, List<String> symptomNames) {
        OWLEntityRemover remover = new OWLEntityRemover(ontoManager, plantOntology.getImportsClosure());

        // Премахване на растението
        OWLNamedIndividual plantIndiv = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + plantName));
        plantIndiv.accept(remover);

        // Премахване на симптомите
        if (symptomNames != null) {
            for (String symptom : symptomNames) {
                String symptomIndivName = plantName + "_" + symptom;
                OWLNamedIndividual symptomIndiv = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + symptomIndivName));
                symptomIndiv.accept(remover);
            }
        }

        ontoManager.applyChanges(remover.getChanges());
    }

    public List<String> getNeedsForPlant(String plantIndivName) {
        List<String> result = new ArrayList<>();
        OWLNamedIndividual plant = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + plantIndivName));
        OWLObjectProperty hasNeed = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasNeed"));

        for (OWLObjectPropertyAssertionAxiom ax : plantOntology.getObjectPropertyAssertionAxioms(plant)) {
            if (ax.getProperty().asOWLObjectProperty().equals(hasNeed)) {
                OWLNamedIndividual needIndiv = ax.getObject().asOWLNamedIndividual();
                StringBuilder sb = new StringBuilder();
                sb.append("Нужда: ").append(getIndividualFriendlyName(needIndiv.getIRI()));

                for (OWLClassExpression type : needIndiv.getTypes(plantOntology)) {
                    if (!type.isAnonymous()) {
                        sb.append(" от тип ").append(getClassFriendlyName(type.asOWLClass()));
                    }
                }

                for (OWLDataPropertyAssertionAxiom dp : plantOntology.getDataPropertyAssertionAxioms(needIndiv)) {
                    OWLDataProperty property = dp.getProperty().asOWLDataProperty();
                    String propName = getIndividualFriendlyName(property.getIRI());
                    sb.append(" | ").append(propName).append(" = ").append(dp.getObject());
                }

                result.add(sb.toString());
            }
        }
        return result;
    }



   public models.Plant getPlantByIndividualName(String plantIndivName) {
        models.Plant plantModel = new models.Plant();
        plantModel.setName(plantIndivName);


        List<String> symptoms = new ArrayList<>();

        OWLNamedIndividual plantIndiv = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + plantIndivName));

       for (OWLClassExpression typeExpr : plantIndiv.getTypes(plantOntology)) {
           if (!typeExpr.isAnonymous()) {
               OWLClass plantClass = typeExpr.asOWLClass();
               plantModel.setType(getClassFriendlyName(plantClass));


                for (OWLSubClassOfAxiom ax : plantOntology.getSubClassAxiomsForSubClass(plantClass)) {
                    if (ax.getSuperClass() instanceof OWLObjectExactCardinality) {
                        OWLObjectExactCardinality cardinality = (OWLObjectExactCardinality) ax.getSuperClass();
                        String iriStr = cardinality.getProperty().asOWLObjectProperty().getIRI().toString();
                        String shortForm = iriStr.substring(iriStr.indexOf("#") + 1);

                        if (shortForm.equals("hasNeed")) {
                            OWLClassExpression needExpr = cardinality.getFiller();
                            if (!needExpr.isAnonymous()) {
                                String needName = getClassFriendlyName(needExpr.asOWLClass());
                                if (needName.contains("Water"))
                                    plantModel.setSoilMoisture(needName.contains("Frequent") ? "high" : "low");
                                if (needName.contains("Humidity"))
                                    plantModel.setHumidity(needName.contains("High") ? "high" : "low");
                                if (needName.contains("Light"))
                                    plantModel.setLight(needName.contains("High") ? "high" : "low");
                                if (needName.contains("Temperature"))
                                    plantModel.setTemperature(needName.contains("High") ? "high" : "low");
                            }
                        }
                    }
                }
            }
        }


        OWLObjectProperty hasSymptom = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasSymptom"));
        for (OWLObjectPropertyAssertionAxiom ax : plantOntology.getObjectPropertyAssertionAxioms(plantIndiv)) {
            if (ax.getProperty().equals(hasSymptom)) {
                OWLNamedIndividual symptom = ax.getObject().asOWLNamedIndividual();
                symptoms.add(getIndividualFriendlyName(symptom.getIRI()));
            }
        }
        plantModel.setSymptoms(symptoms);
        return plantModel;
    }







    public void saveOntology() {
        try {
            ontoManager.saveOntology(plantOntology);
        } catch (OWLOntologyStorageException e) {
            System.out.println("Error saving ontology: " + e.getMessage());
        }
    }



}